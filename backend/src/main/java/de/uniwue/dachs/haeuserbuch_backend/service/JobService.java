package de.uniwue.dachs.haeuserbuch_backend.service;

import de.uniwue.dachs.haeuserbuch_backend.DTO.JobDTO;
import de.uniwue.dachs.haeuserbuch_backend.model.Job;
import de.uniwue.dachs.haeuserbuch_backend.repository.JobRepository;
import de.uniwue.dachs.haeuserbuch_backend.utils.Mappers.JobMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class JobService {
    private final JobRepository jobRepository;
    private final JobMapper jobMapper;

    public JobService(JobRepository jobRepository, JobMapper jobMapper) {
        this.jobRepository = jobRepository;
        this.jobMapper = jobMapper;
    }

    /**
     * GET all occupations
     * @return {@link List} of all {@link Job} objects
     */
    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    /**
     * GET occupation by ID
     * @param id of the occupation
     * @return {@link Optional} of {@link Job}
     */
    public Optional<Job> getJobById(Long id) {
        return jobRepository.findById(id);
    }

    /**
     * POST create a new job
     * @param job {@link Job} to be created
     * @return the created {@link Job}
     */
    @Transactional
    public Job createJob(Job job) {
        return jobRepository.save(job);
    }

    /**
     * PUT update an existing job
     * @param job {@link Job} with updated data
     * @return the updated {@link Job}
     * @throws EntityNotFoundException if the job with the given ID does not exist
     */
    @Transactional
    public Job updateJob(Long id, Job job) {
        return jobRepository.findById(id)
                .map(existingEntity -> {
                    existingEntity.setName(job.getName());
                    existingEntity.setAltNames(job.getAltNames());
                    existingEntity.setDescription(job.getDescription());
                    existingEntity.setInternalNotes(job.getInternalNotes());
                    existingEntity.setGeneralNotes(job.getGeneralNotes());
                    return jobRepository.save(existingEntity);
                })
                .orElseThrow(() -> new EntityNotFoundException("Job with ID " + job.getId() + " does not exist."));
    }

    /**
     * DELETE an occupation by its ID
     * @param id of the occupation to be deleted
     * @throws EntityNotFoundException if the occupation with the given ID does not exist
     */
    @Transactional
    public void deleteJob(Long id) {
        if (!jobRepository.existsById(id)) {
            throw new EntityNotFoundException("Job with ID " + id + " does not exist.");
        }
        jobRepository.deleteById(id);
    }

    public List<JobDTO> searchJobs(String query) {
        return jobRepository.searchJobs(query).stream()
                .map(jobMapper::JobToDTO)
                .filter(Objects::nonNull)
                .toList();
    }
}
