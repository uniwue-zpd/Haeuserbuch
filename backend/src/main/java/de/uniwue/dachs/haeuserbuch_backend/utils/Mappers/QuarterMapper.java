package de.uniwue.dachs.haeuserbuch_backend.utils.Mappers;

import de.uniwue.dachs.haeuserbuch_backend.DTO.QuarterDTO;
import de.uniwue.dachs.haeuserbuch_backend.model.Quarter;
import de.uniwue.dachs.haeuserbuch_backend.repository.QuarterRepository;
import org.springframework.stereotype.Component;

@Component
public class QuarterMapper {
    private final QuarterRepository quarterRepository;

    public QuarterMapper(QuarterRepository quarterRepository) {
        this.quarterRepository = quarterRepository;
    }

    public Quarter QuarterDTOToQuarter(QuarterDTO quarterDTO) {
        if (quarterDTO == null || quarterDTO.getId() == null) {
            return null;
        }
        return quarterRepository.findById(quarterDTO.getId()).orElse(null);
    }

    public QuarterDTO QuarterToDTO(Quarter quarter) {
        if (quarter == null) {
            return null;
        }
        QuarterDTO quarterDTO = new QuarterDTO();
        quarterDTO.setId(quarter.getId());
        quarterDTO.setName(quarter.getName());
        return quarterDTO;
    }
}
