package de.uniwue.dachs.haeuserbuch_backend.utils.Mappers;

import de.uniwue.dachs.haeuserbuch_backend.DTO.PersonOriginDTO;
import de.uniwue.dachs.haeuserbuch_backend.embeddable.PersonOrigin;
import org.springframework.stereotype.Component;

@Component
public class PersonOriginMapper {
    private final PlaceMapper placeMapper;

    public PersonOriginMapper(PlaceMapper placeMapper) {
        this.placeMapper = placeMapper;
    }

    public PersonOrigin DTOToPersonOrigin(PersonOriginDTO personOriginDTO) {
        if (personOriginDTO == null) return null;
        PersonOrigin personOrigin = new PersonOrigin();
        personOrigin.setPlaces(placeMapper.PlaceDTOsToPlaces(personOriginDTO.getPlaces()));
        personOrigin.setOriginalText(personOriginDTO.getOriginalText());
        personOrigin.setCertainty(personOriginDTO.getCertainty());
        return personOrigin;
    }

    public PersonOriginDTO PersonOriginToDTO(PersonOrigin personOrigin) {
        if (personOrigin == null) return null;
        PersonOriginDTO personOriginDTO = new PersonOriginDTO();
        personOriginDTO.setPlaces(placeMapper.PlacesToDTOs(personOrigin.getPlaces()));
        personOriginDTO.setOriginalText(personOrigin.getOriginalText());
        personOriginDTO.setCertainty(personOrigin.getCertainty());
        return personOriginDTO;
    }
}
