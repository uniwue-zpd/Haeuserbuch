package de.uniwue.dachs.haeuserbuch_backend.repository;

import de.uniwue.dachs.haeuserbuch_backend.DTO.Register.RegisterEntryRow;
import de.uniwue.dachs.haeuserbuch_backend.DTO.Register.RegisterRow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import de.uniwue.dachs.haeuserbuch_backend.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    @Query("""
        SELECT DISTINCT j FROM Job j
        LEFT JOIN j.altNames altName
        WHERE LOWER(j.name) LIKE LOWER(CONCAT('%', :query, '%'))
        OR LOWER(altName) LIKE LOWER(CONCAT('%', :query, '%'))
        ORDER BY j.name ASC
    """)
    List<Job> searchJobs(@Param("query") String query);

    /** Job {@code j} matches {@code :name} by name or alternative name */
    String NAME_FILTER = """
        WHERE (LOWER(j.name) LIKE LOWER(CONCAT('%', :name, '%'))
        OR EXISTS (SELECT 1 FROM Job j2 JOIN j2.altNames altName
        WHERE j2 = j AND LOWER(altName) LIKE LOWER(CONCAT('%', :name, '%'))))
    """;

    /** Job {@code j} is the occupation of at least one person */
    String HAS_PERSON_FILTER = """
        AND EXISTS (SELECT 1 FROM Person x WHERE x.job.jobCategory = j)
    """;

    /** Job {@code j} is the occupation of a citizen, guarantor or mentioned person of a citizenship */
    String HAS_CITIZENSHIP_FILTER = """
        AND (EXISTS (SELECT 1 FROM Citizenship c JOIN c.person x WHERE x.job.jobCategory = j)
        OR EXISTS (SELECT 1 FROM Citizenship c JOIN c.guarantors x WHERE x.job.jobCategory = j)
        OR EXISTS (SELECT 1 FROM Citizenship c JOIN c.mentionedPersons x WHERE x.job.jobCategory = j))
    """;

    String REGISTER_SELECT = """
        SELECT new de.uniwue.dachs.haeuserbuch_backend.DTO.Register.RegisterRow(j.id, j.name)
        FROM Job j
    """;

    String REGISTER_ORDER = " ORDER BY LOWER(j.name) ASC, j.id ASC";

    String REGISTER_COUNT = "SELECT COUNT(j) FROM Job j ";

    @Query(
            value = REGISTER_SELECT + NAME_FILTER + HAS_PERSON_FILTER + REGISTER_ORDER,
            countQuery = REGISTER_COUNT + NAME_FILTER + HAS_PERSON_FILTER
    )
    Page<RegisterRow> findPersonRegisterJobs(@Param("name") String name, Pageable pageable);

    @Query(
            value = REGISTER_SELECT + NAME_FILTER + HAS_CITIZENSHIP_FILTER + REGISTER_ORDER,
            countQuery = REGISTER_COUNT + NAME_FILTER + HAS_CITIZENSHIP_FILTER
    )
    Page<RegisterRow> findCitizenshipRegisterJobs(@Param("name") String name, Pageable pageable);

    @Query("""
        SELECT new de.uniwue.dachs.haeuserbuch_backend.DTO.Register.RegisterEntryRow(job.id, person.id, person.fullName)
        FROM Person person
        JOIN person.job.jobCategory job
        WHERE job.id IN :jobIds
    """)
    List<RegisterEntryRow> findPersonEntries(@Param("jobIds") List<Long> jobIds);
}
