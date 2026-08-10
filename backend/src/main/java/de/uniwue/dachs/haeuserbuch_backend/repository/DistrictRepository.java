package de.uniwue.dachs.haeuserbuch_backend.repository;

import de.uniwue.dachs.haeuserbuch_backend.model.District;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DistrictRepository extends JpaRepository<District, Long> {
    @Query("""
        SELECT DISTINCT d FROM District d
        WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :query, '%'))
        ORDER BY d.name ASC
    """)
    List<District> searchDistricts(@Param("query") String query);
}
