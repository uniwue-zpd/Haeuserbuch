package de.uniwue.dachs.haeuserbuch_backend.utils.Mappers;

import de.uniwue.dachs.haeuserbuch_backend.DTO.CitizenshipDTO;
import de.uniwue.dachs.haeuserbuch_backend.DTO.GeoJsonDTO.Feature;
import de.uniwue.dachs.haeuserbuch_backend.DTO.PersonDTO;
import de.uniwue.dachs.haeuserbuch_backend.DTO.SourceDTO;
import de.uniwue.dachs.haeuserbuch_backend.model.Citizenship;
import de.uniwue.dachs.haeuserbuch_backend.model.Person;
import de.uniwue.dachs.haeuserbuch_backend.model.Place;
import de.uniwue.dachs.haeuserbuch_backend.model.Source;
import de.uniwue.dachs.haeuserbuch_backend.repository.PersonRepository;
import de.uniwue.dachs.haeuserbuch_backend.repository.PlaceRepository;
import de.uniwue.dachs.haeuserbuch_backend.repository.SourceRepository;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CitizenshipMapper {
    private final PlaceRepository placeRepository;
    private final SourceRepository sourceRepository;
    private final PersonRepository personRepository;
    private final PersonMapper personMapper;
    private final PlaceMapper placeMapper;

    public CitizenshipMapper(PlaceRepository placeRepository, SourceRepository sourceRepository, PersonRepository personRepository, PersonMapper personMapper, PlaceMapper placeMapper) {
        this.placeRepository = placeRepository;
        this.sourceRepository = sourceRepository;
        this.personRepository = personRepository;
        this.personMapper = personMapper;
        this.placeMapper = placeMapper;
    }

    public Citizenship CitizenshipDTOToCitizenship(CitizenshipDTO citizenshipDTO) {
        Citizenship citizenship = new Citizenship();
        citizenship.setId(citizenshipDTO.getId());
        citizenship.setPersons(getOrSavePersons(citizenshipDTO.getPersons()));
        citizenship.setSource(getSource(citizenshipDTO.getSource()));
        citizenship.setPlace(getPlace(citizenshipDTO.getPlace()));
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
        citizenshipDTO.setPersons(getPersonDTOs(citizenship.getPersons()));
        citizenshipDTO.setSource(getSourceDTO(citizenship.getSource()));
        citizenshipDTO.setPlace(getPlaceFeature(citizenship.getPlace()));
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

    // Persons
        // Get existing persons or save new ones
    public Set<Person> getOrSavePersons(Set<PersonDTO> personDTOs) {
        if (personDTOs == null || personDTOs.isEmpty()) {
            return new HashSet<>();
        }
        return personDTOs.stream()
                .map(person -> {
                    if (person.getId() != null) {
                        return personRepository.findById(person.getId())
                                .orElse(null);
                    } else {
                        Person newPerson = personMapper.PersonDTOToPerson(person);
                        return personRepository.save(newPerson);
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

        // Get PersonDTOs from Persons
    public Set<PersonDTO> getPersonDTOs(Set<Person> persons) {
        if (persons == null || persons.isEmpty()) {
            return new HashSet<>();
        }
        return persons.stream()
                .map(personMapper::PersonToPersonDTO)
                .collect(Collectors.toSet());
    }

    // Source
        // Get Source from SourceDTO
    public Source getSource(SourceDTO sourceDTO) {
        if (sourceDTO == null || sourceDTO.getId() == null) {
            return null;
        }
        return sourceRepository.findById(sourceDTO.getId()).orElse(null);
    }

        // Get SourceDTO from Source
    public SourceDTO getSourceDTO(Source source) {
        if (source == null) {
            return null;
        }
        SourceDTO sourceDTO = new SourceDTO();
        sourceDTO.setId(source.getId());
        sourceDTO.setTitle(source.getTitle());
        return sourceDTO;
    }

    // Place
        // Get Place from Feature
    public Place getPlace(Feature feature) {
        if (feature != null) {
            return placeRepository.findById(feature.getId()).orElse(null);
        }
        return null;
    }

        // Get Feature from Place
    public Feature getPlaceFeature(Place place) {
        if (place != null) {
            return placeMapper.PlaceToFeature(place);
        }
        return null;
    }
}
