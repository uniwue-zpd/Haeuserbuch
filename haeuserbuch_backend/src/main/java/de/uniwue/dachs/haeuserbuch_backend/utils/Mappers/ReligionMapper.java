package de.uniwue.dachs.haeuserbuch_backend.utils.Mappers;

import de.uniwue.dachs.haeuserbuch_backend.DTO.ReligionDTO;
import de.uniwue.dachs.haeuserbuch_backend.model.Religion;
import de.uniwue.dachs.haeuserbuch_backend.repository.ReligionRepository;
import org.springframework.stereotype.Component;

@Component
public class ReligionMapper {
    private final ReligionRepository religionRepository;

    public ReligionMapper(ReligionRepository religionRepository) {
        this.religionRepository = religionRepository;
    }

    public Religion DTOToReligion(ReligionDTO religionDTO) {
        if (religionDTO == null || religionDTO.getId() == null) return null;
        return religionRepository.findById(religionDTO.getId()).orElse(null);
    }

    public ReligionDTO ReligionToDTO(Religion religion) {
        if (religion == null) return null;
        ReligionDTO religionDTO = new ReligionDTO();
        religionDTO.setId(religion.getId());
        religionDTO.setName(religion.getName());
        religionDTO.setDescription(religion.getDescription());
        return religionDTO;
    }
}
