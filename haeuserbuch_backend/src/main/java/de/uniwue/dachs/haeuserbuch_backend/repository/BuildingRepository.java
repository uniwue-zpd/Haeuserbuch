package de.uniwue.dachs.haeuserbuch_backend.repository;

import de.uniwue.dachs.haeuserbuch_backend.model.Building;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BuildingRepository extends JpaRepository<Building, Long> {
    @Override
    @NonNull
    @EntityGraph(attributePaths = {"primarySources"})
    List<Building> findAll();

    List<Building> findAllByDistrict_Id(Long districtId);

    List<Building> findAllByQuarter_Id(Long quarterId);

    List<Building> findAllByCurrentStreet_Id(Long streetId);

    List<Building> findAllByPrimarySources_Id(Long sourceId);

    List<Building> findAllBySecondarySources_Id(Long sourceId);
}
