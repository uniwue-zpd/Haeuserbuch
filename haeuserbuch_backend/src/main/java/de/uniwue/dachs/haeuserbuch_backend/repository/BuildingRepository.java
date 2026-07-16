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

@Repository
public interface BuildingRepository extends JpaRepository<Building, Long>, JpaSpecificationExecutor<Building> {
    @Override
    @NonNull
    @EntityGraph(attributePaths = {"primarySources"})
    List<Building> findAll();

    @Query("""
        SELECT DISTINCT b FROM Building b
        LEFT JOIN b.quarter q
        WHERE LOWER(b.districtHouseNumber) LIKE LOWER(CONCAT('%', :query, '%'))
        OR LOWER(b.object) LIKE LOWER(CONCAT('%', :query, '%'))
        OR LOWER(q.name) LIKE LOWER(CONCAT('%', :query, '%'))
        ORDER BY b.districtHouseNumber ASC
    """)
    List<Building> searchBuildings(@Param("query") String query);
}
