package de.uniwue.dachs.haeuserbuch_backend.utils.Mappers;

import de.uniwue.dachs.haeuserbuch_backend.DTO.PersonReligionDTO;
import de.uniwue.dachs.haeuserbuch_backend.embeddable.PersonReligion;
import org.springframework.stereotype.Component;

@Component
public class PersonReligionMapper {
    private final ReligionMapper religionMapper;

    public PersonReligionMapper(ReligionMapper religionMapper) {
        this.religionMapper = religionMapper;
    }

    public PersonReligion DTOToPersonReligion(PersonReligionDTO personReligionDTO) {
        if (personReligionDTO == null) return new PersonReligion();
        PersonReligion personReligion = new PersonReligion();
        personReligion.setReligionCategory(religionMapper.DTOToReligion(personReligionDTO.getReligionCategory()));
        personReligion.setOriginalText(personReligionDTO.getOriginalText());
        return personReligion;
    }

    public PersonReligionDTO PersonReligionToDTO(PersonReligion personReligion) {
        if (personReligion == null) return new PersonReligionDTO();
        PersonReligionDTO personReligionDTO = new PersonReligionDTO();
        personReligionDTO.setReligionCategory(religionMapper.ReligionToDTO(personReligion.getReligionCategory()));
        personReligionDTO.setOriginalText(personReligion.getOriginalText());
        return personReligionDTO;
    }
}
