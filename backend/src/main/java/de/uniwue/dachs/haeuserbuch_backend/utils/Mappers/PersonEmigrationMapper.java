package de.uniwue.dachs.haeuserbuch_backend.utils.Mappers;

import de.uniwue.dachs.haeuserbuch_backend.DTO.PersonEmigrationDTO;
import de.uniwue.dachs.haeuserbuch_backend.embeddable.PersonEmigration;
import org.springframework.stereotype.Component;

@Component
public class PersonEmigrationMapper {
    private final PlaceMapper placeMapper;

    public PersonEmigrationMapper(PlaceMapper placeMapper) {
        this.placeMapper = placeMapper;
    }

    public PersonEmigration DTOToPersonEmigration(PersonEmigrationDTO personEmigrationDTO) {
        if (personEmigrationDTO == null) return new PersonEmigration();
        PersonEmigration personEmigration = new PersonEmigration();
        // Only link existing places, so an empty autocomplete value does not create a blank place
        if (personEmigrationDTO.getPlace() != null && personEmigrationDTO.getPlace().getId() != null) {
            personEmigration.setPlace(placeMapper.PlaceDTOToPlace(personEmigrationDTO.getPlace()));
        }
        personEmigration.setOriginalText(personEmigrationDTO.getOriginalText());
        return personEmigration;
    }

    public PersonEmigrationDTO PersonEmigrationToDTO(PersonEmigration personEmigration) {
        if (personEmigration == null) return new PersonEmigrationDTO();
        PersonEmigrationDTO personEmigrationDTO = new PersonEmigrationDTO();
        personEmigrationDTO.setPlace(placeMapper.PlaceToDTO(personEmigration.getPlace()));
        personEmigrationDTO.setOriginalText(personEmigration.getOriginalText());
        return personEmigrationDTO;
    }
}
