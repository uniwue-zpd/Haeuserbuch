package de.uniwue.dachs.haeuserbuch_backend.service;

import de.uniwue.dachs.haeuserbuch_backend.model.Quarter;
import de.uniwue.dachs.haeuserbuch_backend.repository.QuarterRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class QuarterService {
    private final QuarterRepository quarterRepository;

    public QuarterService(QuarterRepository quarterRepository) {
        this.quarterRepository = quarterRepository;
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
    public Quarter createQuarter(Quarter quarter) {
        return quarterRepository.save(quarter);
    }

    // PUT update an existing quarter
    @Transactional
    public Quarter updateQuarter(Long id, Quarter updatedQuarter) {
        return quarterRepository.findById(id)
                .map(entity -> {
                    entity.setName(updatedQuarter.getName());
                    entity.setGeneralNotes(updatedQuarter.getGeneralNotes());
                    entity.setInternalNotes(updatedQuarter.getInternalNotes());
                    return quarterRepository.save(entity);
                })
                .orElseThrow(() -> new EntityNotFoundException("Quarter with ID " + id + " does not exist."));
    }

    // DELETE a quarter by its ID
    @Transactional
    public void deleteQuarter(Long id) {
        if (!quarterRepository.existsById(id)) {
            throw new EntityNotFoundException("Quarter with ID " + id + " does not exist.");
        }
        quarterRepository.deleteById(id);
    }
}
