package de.uniwue.dachs.haeuserbuch_backend.service;

import de.uniwue.dachs.haeuserbuch_backend.DTO.StreetDTO;
import de.uniwue.dachs.haeuserbuch_backend.model.Street;
import de.uniwue.dachs.haeuserbuch_backend.repository.StreetRepository;
import de.uniwue.dachs.haeuserbuch_backend.search.SearchIndexAffecting;
import de.uniwue.dachs.haeuserbuch_backend.utils.Mappers.StreetMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class StreetService {
    private final StreetRepository streetRepository;
    private final StreetMapper streetMapper;

    public StreetService(StreetRepository streetRepository, StreetMapper streetMapper) {
        this.streetRepository = streetRepository;
        this.streetMapper = streetMapper;
    }

    // GET all streets
    public List<Street> getAllStreets() {
        return streetRepository.findAll();
    }

    // GET a street by its ID
    public Optional<Street> getStreetById(Long id) {
        return streetRepository.findById(id);
    }

    // POST create a new street
    @Transactional
    @SearchIndexAffecting
    public Street createStreet(Street street) {
        return streetRepository.save(street);
    }

    // PUT update an existing street
    @Transactional
    @SearchIndexAffecting
    public Street updateStreet(Long id, Street street) {
        return streetRepository.findById(id)
                .map(entity -> {
                    entity.setName(street.getName());
                    entity.setAltNames(street.getAltNames());
                    entity.setDescription(street.getDescription());
                    entity.setGeneralNotes(street.getGeneralNotes());
                    entity.setInternalNotes(street.getInternalNotes());
                    return streetRepository.save(entity);
                })
                .orElseThrow(() -> new EntityNotFoundException("Street with ID " + id + " does not exist."));
    }

    // DELETE a street by its ID
    @SearchIndexAffecting
    public void deleteStreet(Long id) {
        if (!streetRepository.existsById(id)) {
            throw new EntityNotFoundException("Street with ID " + id + " does not exist.");
        }
        streetRepository.deleteById(id);
    }

    /**
     * GET An array of streets based on a search query.
     * @param query Search term.
     * @return A {@link List} of {@link StreetDTO} objects matching the search term.
     */
    public List<StreetDTO> searchStreets(String query) {
        return streetRepository.searchStreets(query).stream()
                .map(streetMapper::StreetToDTO)
                .filter(Objects::nonNull)
                .toList();
    }
}
