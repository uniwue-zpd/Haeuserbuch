package de.uniwue.dachs.haeuserbuch_backend.service;

import de.uniwue.dachs.haeuserbuch_backend.DTO.ReligionDTO;
import de.uniwue.dachs.haeuserbuch_backend.model.Religion;
import de.uniwue.dachs.haeuserbuch_backend.repository.ReligionRepository;
import de.uniwue.dachs.haeuserbuch_backend.search.SearchIndexAffecting;
import de.uniwue.dachs.haeuserbuch_backend.utils.Mappers.ReligionMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ReligionService {
    private final ReligionRepository religionRepository;
    private final ReligionMapper religionMapper;

    public ReligionService(ReligionRepository religionRepository, ReligionMapper religionMapper) {
        this.religionRepository = religionRepository;
        this.religionMapper = religionMapper;
    }

    /**
     * GET all religions
     * @return {@link List} of all {@link Religion} objects
     */
    public List<Religion> getAllReligions() {
        return religionRepository.findAll();
    }

    /**
     * GET religion by ID
     * @param id of the religion
     * @return {@link Optional} of {@link Religion}
     */
    public Optional<Religion> getReligionById(Long id) {
        return religionRepository.findById(id);
    }

    /**
     * POST create a new religion
     * @param religion {@link Religion} to be created
     * @return the created {@link Religion}
     */
    @Transactional
    @SearchIndexAffecting
    public Religion createReligion(Religion religion) {
        return religionRepository.save(religion);
    }

    /**
     * PUT update an existing religion
     * @param religion {@link Religion} with updated data
     * @return the updated {@link Religion}
     * @throws EntityNotFoundException if the religion with the given ID does not exist
     */
    @Transactional
    @SearchIndexAffecting
    public Religion updateReligion(Long id, Religion religion) {
        return religionRepository.findById(id)
                .map(existingEntity -> {
                    existingEntity.setName(religion.getName());
                    existingEntity.setDescription(religion.getDescription());
                    return religionRepository.save(existingEntity);
                })
                .orElseThrow(() -> new EntityNotFoundException("Religion with id " + id + " does not exist."));
    }

    /**
     * DELETE a religion by ID
     * @param id of the religion to be deleted
     * @throws EntityNotFoundException if the religion with the given ID does not exist
     */
    @Transactional
    @SearchIndexAffecting
    public void deleteReligion(Long id) {
        if (!religionRepository.existsById(id)) {
            throw new EntityNotFoundException("Religion with id " + id + " does not exist.");
        }
        religionRepository.deleteById(id);
    }

    /**
     * Allows searching for religions based on a search term.
     * @param query Search term.
     * @return A {@link List} of {@link ReligionDTO} objects matching the search criteria.
     */
    public List<ReligionDTO> searchReligions(String query) {
        return religionRepository.searchReligions(query).stream()
                .map(religionMapper::ReligionToDTO)
                .filter(Objects::nonNull)
                .toList();
    }
}
