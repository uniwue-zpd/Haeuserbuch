package de.uniwue.dachs.haeuserbuch_backend.service;

import de.uniwue.dachs.haeuserbuch_backend.model.Job;
import de.uniwue.dachs.haeuserbuch_backend.repository.OccupationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class JobService {
    private final OccupationRepository occupationRepository;

    public JobService(OccupationRepository occupationRepository) {
        this.occupationRepository = occupationRepository;
    }

    /**
     * GET all occupations
     * @return {@link List} of all {@link Job} objects
     */
    public List<Job> getAllOccupations() {
        return occupationRepository.findAll();
    }

    /**
     * GET occupation by ID
     * @param id of the occupation
     * @return {@link Optional} of {@link Job}
     */
    public Optional<Job> getOccupationById(Long id) {
        return occupationRepository.findById(id);
    }

    /**
     * POST create a new job
     * @param job {@link Job} to be created
     * @return the created {@link Job}
     */
    @Transactional
    public Job createOccupation(Job job) {
        return occupationRepository.save(job);
    }

    /**
     * PUT update an existing job
     * @param job {@link Job} with updated data
     * @return the updated {@link Job}
     * @throws EntityNotFoundException if the job with the given ID does not exist
     */
    @Transactional
    public Job updateOccupation(Long id, Job job) {
        return occupationRepository.findById(id)
                .map(existingEntity -> {
                    existingEntity.setName(job.getName());
                    existingEntity.setAltNames(job.getAltNames());
                    existingEntity.setDescription(job.getDescription());
                    existingEntity.setInternalNotes(job.getInternalNotes());
                    existingEntity.setGeneralNotes(job.getGeneralNotes());
                    return occupationRepository.save(existingEntity);
                })
                .orElseThrow(() -> new EntityNotFoundException("Job with ID " + job.getId() + " does not exist."));
    }

    /**
     * DELETE an occupation by its ID
     * @param id of the occupation to be deleted
     * @throws EntityNotFoundException if the occupation with the given ID does not exist
     */
    @Transactional
    public void deleteOccupation(Long id) {
        if (!occupationRepository.existsById(id)) {
            throw new EntityNotFoundException("Job with ID " + id + " does not exist.");
        }
        occupationRepository.deleteById(id);
    }
}
