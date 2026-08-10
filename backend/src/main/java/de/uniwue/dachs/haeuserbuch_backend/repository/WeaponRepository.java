package de.uniwue.dachs.haeuserbuch_backend.repository;

import de.uniwue.dachs.haeuserbuch_backend.model.Weapon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WeaponRepository extends JpaRepository<Weapon, Long> {
    @Query("""
        SELECT DISTINCT w FROM Weapon w
        WHERE LOWER(w.name) LIKE LOWER(CONCAT('%', :query, '%'))
        ORDER BY w.name ASC
    """)
    List<Weapon> searchWeapons(@Param("query") String query);
}
