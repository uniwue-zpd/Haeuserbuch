package de.uniwue.dachs.haeuserbuch_backend.repository;

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
}
