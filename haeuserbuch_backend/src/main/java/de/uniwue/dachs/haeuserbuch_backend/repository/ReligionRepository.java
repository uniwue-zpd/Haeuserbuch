package de.uniwue.dachs.haeuserbuch_backend.repository;

import de.uniwue.dachs.haeuserbuch_backend.model.Religion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReligionRepository extends JpaRepository<Religion, Long> {
    @Query("""
        SELECT DISTINCT r FROM Religion r
        WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :query, '%'))
        ORDER BY r.name ASC
    """)
    List<Religion> searchReligions(@Param("query") String query);
}
