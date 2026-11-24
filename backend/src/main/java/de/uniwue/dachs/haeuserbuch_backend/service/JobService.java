package de.uniwue.dachs.haeuserbuch_backend.service;

import de.uniwue.dachs.haeuserbuch_backend.DTO.JobDTO;
import de.uniwue.dachs.haeuserbuch_backend.DTO.PreviewDTO.CitizenshipPreviewDTO;
import de.uniwue.dachs.haeuserbuch_backend.DTO.PreviewDTO.PersonPreviewDTO;
import de.uniwue.dachs.haeuserbuch_backend.DTO.Register.RegisterDTO;
import de.uniwue.dachs.haeuserbuch_backend.DTO.Register.RegisterEntryRow;
import de.uniwue.dachs.haeuserbuch_backend.DTO.Register.RegisterRow;
import de.uniwue.dachs.haeuserbuch_backend.model.Job;
import de.uniwue.dachs.haeuserbuch_backend.repository.JobRepository;
import de.uniwue.dachs.haeuserbuch_backend.search.SearchIndexAffecting;
import de.uniwue.dachs.haeuserbuch_backend.utils.Mappers.JobMapper;
import de.uniwue.dachs.haeuserbuch_backend.utils.Mappers.PersonMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static de.uniwue.dachs.haeuserbuch_backend.utils.RegisterUtils.*;

@Service
public class JobService {
    private final JobRepository jobRepository;
    private final JobMapper jobMapper;
    private final PersonMapper personMapper;
    private final CitizenshipService citizenshipService;

    public JobService(JobRepository jobRepository,
                      JobMapper jobMapper,
                      PersonMapper personMapper,
                      CitizenshipService citizenshipService) {
        this.jobRepository = jobRepository;
        this.jobMapper = jobMapper;
        this.personMapper = personMapper;
        this.citizenshipService = citizenshipService;
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
    @SearchIndexAffecting
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
    @SearchIndexAffecting
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
    @SearchIndexAffecting
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

    /**
     * Page of occupations of at least one person, each with its persons.
     * Occupations are sorted by name and filtered by name or alternative name.
     */
    @Transactional(readOnly = true)
    public Page<RegisterDTO<PersonPreviewDTO>> getPersonRegister(String name, Pageable pageable) {
        Page<RegisterRow> jobs = jobRepository.findPersonRegisterJobs(normalizeName(name), unsorted(pageable));
        Map<Long, List<PersonPreviewDTO>> persons = personsByGroup(personRows(jobs), personMapper::RegisterEntryRowToPreviewDTO);
        return jobs.map(job -> new RegisterDTO<>(job.id(), job.name(), persons.getOrDefault(job.id(), List.of())));
    }

    /**
     * Page of occupations of a citizen, guarantor or mentioned person of at least one citizenship,
     * each with these citizenships. Occupations are sorted by name and filtered by name or alternative name.
     */
    @Transactional(readOnly = true)
    public Page<RegisterDTO<CitizenshipPreviewDTO>> getCitizenshipRegister(String name, Pageable pageable) {
        Page<RegisterRow> jobs = jobRepository.findCitizenshipRegisterJobs(normalizeName(name), unsorted(pageable));
        Map<Long, List<CitizenshipPreviewDTO>> citizenships =
                citizenshipService.getPreviewsByPersonGroups(personIdsByGroup(personRows(jobs)));
        return jobs.map(job -> new RegisterDTO<>(job.id(), job.name(), citizenships.getOrDefault(job.id(), List.of())));
    }

    /** Persons with one of the given occupations */
    private Stream<RegisterEntryRow> personRows(Page<RegisterRow> jobs) {
        List<Long> jobIds = jobs.map(RegisterRow::id).toList();
        if (jobIds.isEmpty()) return Stream.empty();
        return jobRepository.findPersonEntries(jobIds).stream();
    }
}
