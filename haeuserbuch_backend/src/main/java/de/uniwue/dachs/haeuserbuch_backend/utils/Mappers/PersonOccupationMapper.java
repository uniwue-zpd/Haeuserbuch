package de.uniwue.dachs.haeuserbuch_backend.utils.Mappers;

import de.uniwue.dachs.haeuserbuch_backend.DTO.PersonOccupationDTO;
import de.uniwue.dachs.haeuserbuch_backend.embeddable.PersonOccupation;
import org.springframework.stereotype.Component;

@Component
public class PersonOccupationMapper {
    private final OccupationMapper occupationMapper;

    public PersonOccupationMapper(OccupationMapper occupationMapper) {
        this.occupationMapper = occupationMapper;
    }

    public PersonOccupation DTOToPersonOccupation(PersonOccupationDTO personOccupationDTO) {
        if (personOccupationDTO == null) return new PersonOccupation();
        PersonOccupation personOccupation = new PersonOccupation();
        personOccupation.setOccupationCategory(occupationMapper.DTOToOccupation(personOccupationDTO.getOccupationCategory()));
        personOccupation.setOriginalText(personOccupationDTO.getOriginalText());
        return personOccupation;
    }

    public PersonOccupationDTO PersonOccupationToDTO(PersonOccupation personOccupation) {
        if (personOccupation == null) return new PersonOccupationDTO();
        PersonOccupationDTO personOccupationDTO = new PersonOccupationDTO();
        personOccupationDTO.setOccupationCategory(occupationMapper.OccupationToDTO(personOccupation.getOccupationCategory()));
        personOccupationDTO.setOriginalText(personOccupation.getOriginalText());
        return personOccupationDTO;
    }
}
