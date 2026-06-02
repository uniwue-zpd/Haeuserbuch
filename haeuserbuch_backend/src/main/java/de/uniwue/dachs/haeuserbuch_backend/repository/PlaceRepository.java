package de.uniwue.dachs.haeuserbuch_backend.repository;

import de.uniwue.dachs.haeuserbuch_backend.model.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {
    @Query("""
        SELECT DISTINCT p FROM Place p
        LEFT JOIN p.altNames altName
        WHERE LOWER(p.realName) LIKE LOWER(CONCAT('%', :query, '%'))
        OR LOWER(altName) LIKE LOWER(CONCAT('%', :query, '%'))
        ORDER BY p.realName ASC
    """)
    List<Place> searchPlaces(@Param("query") String query);
}
