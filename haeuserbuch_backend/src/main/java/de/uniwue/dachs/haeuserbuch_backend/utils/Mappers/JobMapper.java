package de.uniwue.dachs.haeuserbuch_backend.utils.Mappers;

import de.uniwue.dachs.haeuserbuch_backend.DTO.JobDTO;
import de.uniwue.dachs.haeuserbuch_backend.model.Job;
import de.uniwue.dachs.haeuserbuch_backend.repository.OccupationRepository;
import org.springframework.stereotype.Component;

@Component
public class JobMapper {
    private final OccupationRepository occupationRepository;

    public JobMapper(OccupationRepository occupationRepository) {
        this.occupationRepository = occupationRepository;
    }

    public Job DTOToJob(JobDTO jobDTO) {
        if (jobDTO == null || jobDTO.getId() == null) return null;
        return occupationRepository.findById(jobDTO.getId()).orElse(null);
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
