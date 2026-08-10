package de.uniwue.dachs.haeuserbuch_backend.repository;

import de.uniwue.dachs.haeuserbuch_backend.model.Quarter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuarterRepository extends JpaRepository<Quarter, Long> {
    @Query("""
        SELECT DISTINCT q FROM Quarter q
        WHERE LOWER(q.name) LIKE LOWER(CONCAT('%', :query, '%'))
        ORDER BY q.name ASC
    """)
    List<Quarter> searchQuarters(@Param("query") String query);
}
