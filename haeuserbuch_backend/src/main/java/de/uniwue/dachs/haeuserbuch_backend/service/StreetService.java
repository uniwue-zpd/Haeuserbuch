package de.uniwue.dachs.haeuserbuch_backend.service;

import de.uniwue.dachs.haeuserbuch_backend.model.Street;
import de.uniwue.dachs.haeuserbuch_backend.repository.StreetRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class StreetService {
    private final StreetRepository streetRepository;

    public StreetService(StreetRepository streetRepository) {
        this.streetRepository = streetRepository;
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
    public Street createStreet(Street street) {
        return streetRepository.save(street);
    }

    // PUT update an existing street
    @Transactional
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
    public void deleteStreet(Long id) {
        if (!streetRepository.existsById(id)) {
            throw new EntityNotFoundException("Street with ID " + id + " does not exist.");
        }
        streetRepository.deleteById(id);
    }
}
