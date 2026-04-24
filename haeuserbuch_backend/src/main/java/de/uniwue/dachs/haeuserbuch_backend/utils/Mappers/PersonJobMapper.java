package de.uniwue.dachs.haeuserbuch_backend.utils.Mappers;

import de.uniwue.dachs.haeuserbuch_backend.DTO.PersonJobDTO;
import de.uniwue.dachs.haeuserbuch_backend.embeddable.PersonJob;
import org.springframework.stereotype.Component;

@Component
public class PersonJobMapper {
    private final JobMapper jobMapper;

    public PersonJobMapper(JobMapper jobMapper) {
        this.jobMapper = jobMapper;
    }

    public PersonJob DTOToPersonJob(PersonJobDTO personJobDTO) {
        if (personJobDTO == null) return new PersonJob();
        PersonJob personJob = new PersonJob();
        personJob.setJobCategory(jobMapper.DTOToJob(personJobDTO.getJobCategory()));
        personJob.setOriginalText(personJobDTO.getOriginalText());
        return personJob;
    }

    public PersonJobDTO PersonJobToDTO(PersonJob personJob) {
        if (personJob == null) return new PersonJobDTO();
        PersonJobDTO personJobDTO = new PersonJobDTO();
        personJobDTO.setJobCategory(jobMapper.JobToDTO(personJob.getJobCategory()));
        personJobDTO.setOriginalText(personJob.getOriginalText());
        return personJobDTO;
    }
}
