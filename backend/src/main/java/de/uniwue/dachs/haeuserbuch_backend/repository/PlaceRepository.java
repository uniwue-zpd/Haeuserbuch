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

    /** Place {@code p} matches {@code :name} by real or alternative name */
    String NAME_FILTER = """
        WHERE (LOWER(p.realName) LIKE LOWER(CONCAT('%', :name, '%'))
        OR EXISTS (SELECT 1 FROM Place p2 JOIN p2.altNames altName
        WHERE p2 = p AND LOWER(altName) LIKE LOWER(CONCAT('%', :name, '%'))))
    """;

    /** Place {@code p} is the origin or emigration place of at least one person */
    String HAS_PERSON_FILTER = """
        AND (EXISTS (SELECT 1 FROM Person o JOIN o.origin.places op WHERE op = p)
        OR EXISTS (SELECT 1 FROM Person e WHERE e.emigration.place = p))
    """;

    /**
     * Place {@code p} is the origin or emigration place of a citizen, guarantor or mentioned person of a citizenship.
     * Only plain joins and equality conditions (no OR inside a join), so PostgreSQL can resolve each EXISTS via indexes.
     */
    String HAS_CITIZENSHIP_FILTER = """
        AND (EXISTS (SELECT 1 FROM Citizenship c JOIN c.person x JOIN x.origin.places op WHERE op = p)
        OR EXISTS (SELECT 1 FROM Citizenship c JOIN c.person x WHERE x.emigration.place = p)
        OR EXISTS (SELECT 1 FROM Citizenship c JOIN c.guarantors x JOIN x.origin.places op WHERE op = p)
        OR EXISTS (SELECT 1 FROM Citizenship c JOIN c.guarantors x WHERE x.emigration.place = p)
        OR EXISTS (SELECT 1 FROM Citizenship c JOIN c.mentionedPersons x JOIN x.origin.places op WHERE op = p)
        OR EXISTS (SELECT 1 FROM Citizenship c JOIN c.mentionedPersons x WHERE x.emigration.place = p))
    """;

    String REGISTER_SELECT = """
        SELECT new de.uniwue.dachs.haeuserbuch_backend.DTO.Register.PlaceRegisterRow(p.id, p.realName)
        FROM Place p
    """;

    String REGISTER_ORDER = " ORDER BY LOWER(p.realName) ASC, p.id ASC";

    String REGISTER_COUNT = "SELECT COUNT(p) FROM Place p ";

    @Query(
            value = REGISTER_SELECT + NAME_FILTER + HAS_PERSON_FILTER + REGISTER_ORDER,
            countQuery = REGISTER_COUNT + NAME_FILTER + HAS_PERSON_FILTER
    )
    Page<PlaceRegisterRow> findPersonRegisterPlaces(@Param("name") String name, Pageable pageable);

    @Query(
            value = REGISTER_SELECT + NAME_FILTER + HAS_CITIZENSHIP_FILTER + REGISTER_ORDER,
            countQuery = REGISTER_COUNT + NAME_FILTER + HAS_CITIZENSHIP_FILTER
    )
    Page<PlaceRegisterRow> findCitizenshipRegisterPlaces(@Param("name") String name, Pageable pageable);

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
