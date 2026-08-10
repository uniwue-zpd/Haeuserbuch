package de.uniwue.dachs.haeuserbuch_backend.repository;

import de.uniwue.dachs.haeuserbuch_backend.model.Street;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StreetRepository extends JpaRepository<Street, Long> {
    @Query("""
        SELECT DISTINCT s FROM Street s
        LEFT JOIN s.altNames altName
        WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :query, '%'))
        OR LOWER(altName) LIKE LOWER(CONCAT('%', :query, '%'))
        ORDER BY s.name ASC
    """)
    List<Street> searchStreets(@Param("query") String query);
}
