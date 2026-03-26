package de.uniwue.dachs.haeuserbuch_backend.service;

import de.uniwue.dachs.haeuserbuch_backend.model.Occupation;
import de.uniwue.dachs.haeuserbuch_backend.repository.OccupationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class OccupationService {
    private final OccupationRepository occupationRepository;

    public OccupationService(OccupationRepository occupationRepository) {
        this.occupationRepository = occupationRepository;
    }

    /**
     * GET all occupations
     * @return {@link List} of all {@link Occupation} objects
     */
    public List<Occupation> getAllOccupations() {
        return occupationRepository.findAll();
    }

    /**
     * GET occupation by ID
     * @param id of the occupation
     * @return {@link Optional} of {@link Occupation}
     */
    public Optional<Occupation> getOccupationById(Long id) {
        return occupationRepository.findById(id);
    }

    /**
     * POST create a new occupation
     * @param occupation {@link Occupation} to be created
     * @return the created {@link Occupation}
     */
    @Transactional
    public Occupation createOccupation(Occupation occupation) {
        return occupationRepository.save(occupation);
    }

    /**
     * PUT update an existing occupation
     * @param occupation {@link Occupation} with updated data
     * @return the updated {@link Occupation}
     * @throws EntityNotFoundException if the occupation with the given ID does not exist
     */
    @Transactional
    public Occupation updateOccupation(Long id, Occupation occupation) {
        return occupationRepository.findById(id)
                .map(existingEntity -> {
                    existingEntity.setName(occupation.getName());
                    existingEntity.setDescription(occupation.getDescription());
                    return occupationRepository.save(existingEntity);
                })
                .orElseThrow(() -> new EntityNotFoundException("Occupation with ID " + occupation.getId() + " does not exist."));
    }

    /**
     * DELETE an occupation by its ID
     * @param id of the occupation to be deleted
     * @throws EntityNotFoundException if the occupation with the given ID does not exist
     */
    @Transactional
    public void deleteOccupation(Long id) {
        if (!occupationRepository.existsById(id)) {
            throw new EntityNotFoundException("Occupation with ID " + id + " does not exist.");
        }
        occupationRepository.deleteById(id);
    }
}
