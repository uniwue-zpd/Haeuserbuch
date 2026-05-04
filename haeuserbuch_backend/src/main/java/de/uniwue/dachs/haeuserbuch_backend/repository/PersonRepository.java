package de.uniwue.dachs.haeuserbuch_backend.repository;

import de.uniwue.dachs.haeuserbuch_backend.model.Person;
import lombok.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long>, JpaSpecificationExecutor<Person> {
    @Override
    @NonNull
    @EntityGraph(attributePaths = {
            "associatedBuilding",
            "job.jobCategory",
            "religion.religionCategory",
            "altNames"
    })
    List<Person> findAll();

    @Query("""
    SELECT DISTINCT p FROM Person p
        LEFT JOIN p.altNames altName
        WHERE LOWER(p.fullName) LIKE LOWER(CONCAT('%', :query, '%'))
        OR LOWER(altName) LIKE LOWER(CONCAT('%', :query, '%'))
        ORDER BY p.fullName ASC
    """)
    List<Person> searchPeople(@Param("query") String query);
}
