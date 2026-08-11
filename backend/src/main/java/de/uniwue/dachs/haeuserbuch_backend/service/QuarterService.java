package de.uniwue.dachs.haeuserbuch_backend.service;

import de.uniwue.dachs.haeuserbuch_backend.DTO.QuarterDTO;
import de.uniwue.dachs.haeuserbuch_backend.model.Quarter;
import de.uniwue.dachs.haeuserbuch_backend.repository.QuarterRepository;
import de.uniwue.dachs.haeuserbuch_backend.search.SearchIndexAffecting;
import de.uniwue.dachs.haeuserbuch_backend.utils.Mappers.QuarterMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class QuarterService {
    private final QuarterRepository quarterRepository;
    private final QuarterMapper quarterMapper;

    public QuarterService(QuarterRepository quarterRepository, QuarterMapper quarterMapper) {
        this.quarterRepository = quarterRepository;
        this.quarterMapper = quarterMapper;
    }

    // GET all quarters
    public List<Quarter> getAllQuarters() {
        return quarterRepository.findAll();
    }

    // GET a quarter by its ID
    public Optional<Quarter> getQuarterById(Long id) {
        return quarterRepository.findById(id);
    }

    // POST create a new quarter
    @Transactional
    @SearchIndexAffecting
    public Quarter createQuarter(Quarter quarter) {
        return quarterRepository.save(quarter);
    }

    // PUT update an existing quarter
    @Transactional
    @SearchIndexAffecting
    public Quarter updateQuarter(Long id, Quarter updatedQuarter) {
        return quarterRepository.findById(id)
                .map(entity -> {
                    entity.setName(updatedQuarter.getName());
                    entity.setDescription(updatedQuarter.getDescription());
                    entity.setGeneralNotes(updatedQuarter.getGeneralNotes());
                    entity.setInternalNotes(updatedQuarter.getInternalNotes());
                    return quarterRepository.save(entity);
                })
                .orElseThrow(() -> new EntityNotFoundException("Quarter with ID " + id + " does not exist."));
    }

    // DELETE a quarter by its ID
    @Transactional
    @SearchIndexAffecting
    public void deleteQuarter(Long id) {
        if (!quarterRepository.existsById(id)) {
            throw new EntityNotFoundException("Quarter with ID " + id + " does not exist.");
        }
        quarterRepository.deleteById(id);
    }

    /**
     * GET An array of quarters based on a search query.
     * @param query Search term.
     * @return A {@link List} of {@link QuarterDTO} objects matching the search term.
     */
    public List<QuarterDTO> searchQuarters(String query) {
        return quarterRepository.searchQuarters(query).stream()
                .map(quarterMapper::QuarterToDTO)
                .filter(Objects::nonNull)
                .toList();
    }
}
