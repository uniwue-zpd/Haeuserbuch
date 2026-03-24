package de.uniwue.dachs.haeuserbuch_backend.utils.Mappers;

import de.uniwue.dachs.haeuserbuch_backend.DTO.CitizenshipDTO;
import de.uniwue.dachs.haeuserbuch_backend.model.Citizenship;
import org.springframework.stereotype.Component;

@Component
public class CitizenshipMapper {
    private final PersonMapper personMapper;
    private final SourceMapper sourceMapper;

    public CitizenshipMapper(PersonMapper personMapper, SourceMapper sourceMapper) {
        this.personMapper = personMapper;
        this.sourceMapper = sourceMapper;
    }

    public Citizenship DTOToCitizenship(CitizenshipDTO citizenshipDTO) {
        Citizenship citizenship = new Citizenship();
        citizenship.setSignature(citizenshipDTO.getSignature());
        citizenship.setPerson(personMapper.PreviewDTOToPerson(citizenshipDTO.getPerson()));
        citizenship.setMentionedPersons(personMapper.PreviewDTOsToPersons(citizenshipDTO.getMentionedPersons()));
        citizenship.setPrimarySource(sourceMapper.SourceDTOToSource(citizenshipDTO.getPrimarySource()));
        citizenship.setSecondarySource(sourceMapper.SourceDTOToSource(citizenshipDTO.getSecondarySource()));
        citizenship.setRefNumber(citizenshipDTO.getRefNumber());
        citizenship.setDateNaturalization(citizenshipDTO.getDateNaturalization());
        citizenship.setDateMisc(citizenshipDTO.getDateMisc());
        citizenship.setEntryText(citizenshipDTO.getEntryText());
        citizenship.setAddendum(citizenshipDTO.getAddendum());
        citizenship.setInternalNotes(citizenshipDTO.getInternalNotes());
        citizenship.setGeneralNotes(citizenshipDTO.getGeneralNotes());
        return citizenship;
    }

    public CitizenshipDTO CitizenshipToDTO(Citizenship citizenship) {
        CitizenshipDTO citizenshipDTO = new CitizenshipDTO();
        citizenshipDTO.setId(citizenship.getId());
        citizenshipDTO.setSignature(citizenship.getSignature());
        citizenshipDTO.setPerson(personMapper.PersonToPreviewDTO(citizenship.getPerson()));
        citizenshipDTO.setMentionedPersons(personMapper.PersonsToPreviewDTOs(citizenship.getMentionedPersons()));
        citizenshipDTO.setPrimarySource(sourceMapper.SourceToDTO(citizenship.getPrimarySource()));
        citizenshipDTO.setSecondarySource(sourceMapper.SourceToDTO(citizenship.getSecondarySource()));
        citizenshipDTO.setRefNumber(citizenship.getRefNumber());
        citizenshipDTO.setDateNaturalization(citizenship.getDateNaturalization());
        citizenshipDTO.setDateMisc(citizenship.getDateMisc());
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
