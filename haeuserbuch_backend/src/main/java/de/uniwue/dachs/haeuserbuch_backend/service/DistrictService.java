package de.uniwue.dachs.haeuserbuch_backend.service;

import de.uniwue.dachs.haeuserbuch_backend.DTO.DistrictDTO;
import de.uniwue.dachs.haeuserbuch_backend.model.District;
import de.uniwue.dachs.haeuserbuch_backend.repository.DistrictRepository;
import de.uniwue.dachs.haeuserbuch_backend.utils.Mappers.DistrictMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class DistrictService {
    private final DistrictRepository districtRepository;
    private final DistrictMapper districtMapper;

    public DistrictService(DistrictRepository districtRepository, DistrictMapper districtMapper) {
        this.districtRepository = districtRepository;
        this.districtMapper = districtMapper;
    }

    // GET all districts
    public List<District> getAllDistricts() {
        return districtRepository.findAll();
    }

    // GET a district by its ID
    public Optional<District> getDistrictById(Long id) {
        return districtRepository.findById(id);
    }

    // POST create a new district
    @Transactional
    public District createDistrict(District district) {
        return districtRepository.save(district);
    }

    // PUT update an existing district
    @Transactional
    public District updateDistrict(Long id, District updatedDistrict) {
        return districtRepository.findById(id)
                .map(entity -> {
                    entity.setName(updatedDistrict.getName());
                    entity.setDescription(updatedDistrict.getDescription());
                    entity.setGeneralNotes(updatedDistrict.getGeneralNotes());
                    entity.setInternalNotes(updatedDistrict.getInternalNotes());
                    return districtRepository.save(entity);
                })
                .orElseThrow(() -> new EntityNotFoundException("District with ID " + id + " does not exist."));
    }

    // DELETE a district by its ID
    @Transactional
    public void deleteDistrict(Long id) {
        if (!districtRepository.existsById(id)) {
            throw new EntityNotFoundException("District with ID " + id + " does not exist.");
        }
        districtRepository.deleteById(id);
    }

    /**
     * GET An array of districts based on a search term.
     * @param query Search term.
     * @return A {@link List} of {@link DistrictDTO} objects matching the search term.
     */
    public List<DistrictDTO> searchDistricts(String query) {
        return districtRepository.searchDistricts(query).stream()
                .map(districtMapper::DistrictToDTO)
                .filter(Objects::nonNull)
                .toList();
    }
}
