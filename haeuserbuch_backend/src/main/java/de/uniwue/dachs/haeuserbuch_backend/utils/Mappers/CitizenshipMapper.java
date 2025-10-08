package de.uniwue.dachs.haeuserbuch_backend.utils.Mappers;

import de.uniwue.dachs.haeuserbuch_backend.DTO.CitizenshipDTO;
import de.uniwue.dachs.haeuserbuch_backend.model.Citizenship;
import org.springframework.stereotype.Component;

@Component
public class CitizenshipMapper {
    private final PersonMapper personMapper;
    private final PlaceMapper placeMapper;
    private final SourceMapper sourceMapper;

    public CitizenshipMapper(PersonMapper personMapper, PlaceMapper placeMapper, SourceMapper sourceMapper) {
        this.personMapper = personMapper;
        this.placeMapper = placeMapper;
        this.sourceMapper = sourceMapper;
    }

    public Citizenship CitizenshipDTOToCitizenship(CitizenshipDTO citizenshipDTO) {
        Citizenship citizenship = new Citizenship();
        citizenship.setId(citizenshipDTO.getId());
        citizenship.setPersons(personMapper.PersonDTOsToPersons(citizenshipDTO.getPersons()));
        citizenship.setSource(sourceMapper.SourceDTOToSource(citizenshipDTO.getSource()));
        citizenship.setPlace(placeMapper.PlaceDTOToPlace(citizenshipDTO.getPlace()));
        citizenship.setNumber(citizenshipDTO.getNumber());
        citizenship.setDate(citizenshipDTO.getDate());
        citizenship.setEntryText(citizenshipDTO.getEntryText());
        citizenship.setAddendum(citizenshipDTO.getAddendum());
        citizenship.setInternalNotes(citizenshipDTO.getInternalNotes());
        citizenship.setGeneralNotes(citizenshipDTO.getGeneralNotes());
        return citizenship;
    }

    public CitizenshipDTO CitizenshipToDTO(Citizenship citizenship) {
        CitizenshipDTO citizenshipDTO = new CitizenshipDTO();
        citizenshipDTO.setId(citizenship.getId());
        citizenshipDTO.setPersons(personMapper.PersonsToPersonDTOs(citizenship.getPersons()));
        citizenshipDTO.setSource(sourceMapper.SourceToDTO(citizenship.getSource()));
        citizenshipDTO.setPlace(placeMapper.PlaceToDTO(citizenship.getPlace()));
        citizenshipDTO.setNumber(citizenship.getNumber());
        citizenshipDTO.setDate(citizenship.getDate());
        citizenshipDTO.setEntryText(citizenship.getEntryText());
        citizenshipDTO.setAddendum(citizenship.getAddendum());
        citizenshipDTO.setInternalNotes(citizenship.getInternalNotes());
        citizenshipDTO.setGeneralNotes(citizenship.getGeneralNotes());
        citizenshipDTO.setCreatedBy(citizenship.getCreatedBy());
        citizenshipDTO.setCreatedDate(citizenship.getCreatedDate());
        citizenshipDTO.setLastModifiedBy(citizenship.getLastModifiedBy());
        citizenshipDTO.setLastModifiedDate(citizenship.getLastModifiedDate());
        return citizenshipDTO;
    }
}
