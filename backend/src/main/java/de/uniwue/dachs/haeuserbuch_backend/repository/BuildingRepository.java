package de.uniwue.dachs.haeuserbuch_backend.repository;

import de.uniwue.dachs.haeuserbuch_backend.model.Building;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BuildingRepository extends JpaRepository<Building, Long>, JpaSpecificationExecutor<Building> {
    @Override
    @NonNull
    @EntityGraph(attributePaths = {"sources", "files"})
    List<Building> findAll();

    @Override
    @EntityGraph(attributePaths = {"sources", "files"})
    Optional<Building> findById(@NonNull Long id);

    List<Building> findByFilesId(Long fileId);

    @Query("""
        SELECT DISTINCT b FROM Building b
        LEFT JOIN b.quarter q
        WHERE LOWER(b.districtPropertyNumber) LIKE LOWER(CONCAT('%', :query, '%'))
        OR LOWER(b.object) LIKE LOWER(CONCAT('%', :query, '%'))
        OR LOWER(q.name) LIKE LOWER(CONCAT('%', :query, '%'))
        ORDER BY b.districtPropertyNumber ASC
    """)
    List<Building> searchBuildings(@Param("query") String query);
}
