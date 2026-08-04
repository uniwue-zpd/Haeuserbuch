package de.uniwue.dachs.haeuserbuch_backend.repository;

import de.uniwue.dachs.haeuserbuch_backend.model.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {
    @Query("""
    SELECT DISTINCT f FROM File f
    WHERE LOWER(f.name) LIKE LOWER(CONCAT('%', :query, '%'))
    ORDER BY f.name ASC
    """)
    List<File> searchFiles(@Param("query") String query);
}
