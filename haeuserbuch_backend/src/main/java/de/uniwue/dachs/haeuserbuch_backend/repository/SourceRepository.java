package de.uniwue.dachs.haeuserbuch_backend.repository;

import de.uniwue.dachs.haeuserbuch_backend.model.Source;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SourceRepository extends JpaRepository<Source, Long> {
    @Query("""
        SELECT DISTINCT s FROM Source s
        LEFT JOIN s.authors author
        WHERE LOWER(s.title) LIKE LOWER(CONCAT('%', :query, '%'))
        OR LOWER(s.type) LIKE LOWER(CONCAT('%', :query, '%'))
        OR LOWER(s.signature) LIKE LOWER(CONCAT('%', :query, '%'))
        OR LOWER(author) LIKE LOWER(CONCAT('%', :query, '%'))
        ORDER BY s.title ASC
    """)
    List<Source> searchSources(@Param("query") String query);
}
