package de.uniwue.dachs.haeuserbuch_backend.utils.Mappers;

import de.uniwue.dachs.haeuserbuch_backend.DTO.JobDTO;
import de.uniwue.dachs.haeuserbuch_backend.model.Job;
import de.uniwue.dachs.haeuserbuch_backend.repository.JobRepository;
import org.springframework.stereotype.Component;

@Component
public class JobMapper {
    private final JobRepository jobRepository;

    public JobMapper(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public Job DTOToJob(JobDTO jobDTO) {
        if (jobDTO == null || jobDTO.getId() == null) return null;
        return jobRepository.findById(jobDTO.getId()).orElse(null);
    }

    public JobDTO JobToDTO(Job job) {
        if (job == null) return null;
        JobDTO jobDTO = new JobDTO();
        jobDTO.setId(job.getId());
        jobDTO.setName(job.getName());
        jobDTO.setDescription(job.getDescription());
        return jobDTO;
    }
}
