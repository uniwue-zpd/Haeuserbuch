package de.uniwue.dachs.haeuserbuch_backend.repository;

import de.uniwue.dachs.haeuserbuch_backend.DTO.Register.PlaceEntryRow;
import de.uniwue.dachs.haeuserbuch_backend.DTO.Register.PlaceRegisterRow;
import de.uniwue.dachs.haeuserbuch_backend.model.Place;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    /** Places that are the origin or emigration place of at least one person, matched by real or alternative name */
    String PERSON_REGISTER_FILTER = """
        WHERE (LOWER(p.realName) LIKE LOWER(CONCAT('%', :name, '%'))
        OR EXISTS (SELECT 1 FROM Place p2 JOIN p2.altNames altName
        WHERE p2 = p AND LOWER(altName) LIKE LOWER(CONCAT('%', :name, '%'))))
        AND (EXISTS (SELECT 1 FROM Person o JOIN o.origin.places op WHERE op = p)
        OR EXISTS (SELECT 1 FROM Person e WHERE e.emigration.place = p))
    """;

    @Query(
            value = """
                SELECT new de.uniwue.dachs.haeuserbuch_backend.DTO.Register.PlaceRegisterRow(p.id, p.realName)
                FROM Place p
            """ + PERSON_REGISTER_FILTER +
            """
                ORDER BY LOWER(p.realName) ASC, p.id ASC
            """,
            countQuery = "SELECT COUNT(p) FROM Place p " + PERSON_REGISTER_FILTER
    )
    Page<PlaceRegisterRow> findPersonRegisterPlaces(@Param("name") String name, Pageable pageable);

    @Query("""
        SELECT new de.uniwue.dachs.haeuserbuch_backend.DTO.Register.PlaceEntryRow(place.id, person.id, person.fullName)
        FROM Person person
        JOIN person.origin.places place
        WHERE place.id IN :placeIds
    """)
    List<PlaceEntryRow> findOriginPlaceEntries(@Param("placeIds") List<Long> placeIds);

    @Query("""
        SELECT new de.uniwue.dachs.haeuserbuch_backend.DTO.Register.PlaceEntryRow(place.id, person.id, person.fullName)
        FROM Person person
        JOIN person.emigration.place place
        WHERE place.id IN :placeIds
    """)
    List<PlaceEntryRow> findEmigrationPlaceEntries(@Param("placeIds") List<Long> placeIds);
}
