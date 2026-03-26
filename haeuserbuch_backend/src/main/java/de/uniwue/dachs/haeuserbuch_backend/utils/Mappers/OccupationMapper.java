package de.uniwue.dachs.haeuserbuch_backend.utils.Mappers;

import de.uniwue.dachs.haeuserbuch_backend.DTO.OccupationDTO;
import de.uniwue.dachs.haeuserbuch_backend.model.Occupation;
import de.uniwue.dachs.haeuserbuch_backend.repository.OccupationRepository;
import org.springframework.stereotype.Component;

@Component
public class OccupationMapper {
    private final OccupationRepository occupationRepository;

    public OccupationMapper(OccupationRepository occupationRepository) {
        this.occupationRepository = occupationRepository;
    }

    public Occupation DTOToOccupation(OccupationDTO occupationDTO) {
        if (occupationDTO == null || occupationDTO.getId() == null) return null;
        return occupationRepository.findById(occupationDTO.getId()).orElse(null);
    }

    public OccupationDTO OccupationToDTO(Occupation occupation) {
        if (occupation == null) return null;
        OccupationDTO occupationDTO = new OccupationDTO();
        occupationDTO.setId(occupation.getId());
        occupationDTO.setName(occupation.getName());
        occupationDTO.setDescription(occupation.getDescription());
        return occupationDTO;
    }
}
