package de.uniwue.dachs.haeuserbuch_backend.utils.Mappers;

import de.uniwue.dachs.haeuserbuch_backend.DTO.PersonDTO;
import de.uniwue.dachs.haeuserbuch_backend.DTO.PreviewDTO.PersonPreviewDTO;
import de.uniwue.dachs.haeuserbuch_backend.model.Person;
import de.uniwue.dachs.haeuserbuch_backend.repository.PersonRepository;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class PersonMapper {
    private final PersonRepository personRepository;
    private final PlaceMapper placeMapper;
    private final BuildingMapper buildingMapper;

    public PersonMapper(PersonRepository personRepository, PlaceMapper placeMapper, BuildingMapper buildingMapper) {
        this.personRepository = personRepository;
        this.placeMapper = placeMapper;
        this.buildingMapper = buildingMapper;
    }

    public Person PersonDTOToPerson(PersonDTO personDTO) {
        if (personDTO == null) {
            return null;
        }
        if (personDTO.getId() != null) {
            return personRepository.findById(personDTO.getId()).orElse(null);
        } else {
            Person person = new Person();
            person.setFirstName(personDTO.getFirstName());
            person.setLastName(personDTO.getLastName());
            person.setFullName(personDTO.getFullName());
            person.setAltNames(personDTO.getAltNames());
            person.setSex(personDTO.getSex());
            person.setOccupation(personDTO.getOccupation());
            person.setOccupationCategory(personDTO.getOccupationCategory());
            person.setAssociatedBuilding(buildingMapper.buildingDTOToBuilding(personDTO.getAssociatedBuilding()));
            person.setIsCitizen(personDTO.getIsCitizen());
            person.setConfession(personDTO.getConfession());
            person.setOrigin(placeMapper.PlaceDTOToPlace(personDTO.getOrigin()));
            personRepository.save(person);
            return person;
        }
    }

    public Set<Person> PersonDTOsToPersons(Set<PersonDTO> personDTOs) {
        return personDTOs.stream().map(this::PersonDTOToPerson).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    public Person PersonPreviewDTOToPerson(PersonPreviewDTO previewDTO) {
        if (previewDTO == null) return null;
        if (previewDTO.getId() != null) {
            return personRepository.findById(previewDTO.getId()).orElse(null);
        } else {
            Person person = new Person();
            person.setFirstName(previewDTO.getFirstName());
            person.setLastName(previewDTO.getLastName());
            personRepository.save(person);
            return person;
        }
    }

    public Set<Person> PersonPreviewDTOsToPersons(Set<PersonPreviewDTO> previewDTOs) {
        return previewDTOs.stream().map(this::PersonPreviewDTOToPerson).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    public PersonDTO PersonToPersonDTO(Person person) {
        PersonDTO personDTO = new PersonDTO();
        personDTO.setId(person.getId());
        personDTO.setFirstName(person.getFirstName());
        personDTO.setLastName(person.getLastName());
        personDTO.setFullName(person.getFullName());
        personDTO.setAltNames(person.getAltNames());
        personDTO.setSex(person.getSex());
        personDTO.setOccupation(person.getOccupation());
        personDTO.setOccupationCategory(person.getOccupationCategory());
        personDTO.setAssociatedBuilding(buildingMapper.buildingToBuildingDTO(person.getAssociatedBuilding()));
        personDTO.setIsCitizen(person.getIsCitizen());
        personDTO.setConfession(person.getConfession());
        personDTO.setOrigin(placeMapper.PlaceToDTO(person.getOrigin()));
        return personDTO;
    }

    public Set<PersonDTO> PersonsToPersonDTOs(Set<Person> persons) {
        return persons.stream().map(this::PersonToPersonDTO).collect(Collectors.toSet());
    }

    public PersonPreviewDTO PersonToPreviewDTO(Person person) {
        if (person == null) return null;
        PersonPreviewDTO previewDTO = new PersonPreviewDTO();
        previewDTO.setId(person.getId());
        previewDTO.setFirstName(person.getFirstName());
        previewDTO.setLastName(person.getLastName());
        return previewDTO;
    }

    public Set<PersonPreviewDTO> PersonsToPreviewDTOs(Set<Person> persons) {
        return persons.stream().map(this::PersonToPreviewDTO).collect(Collectors.toSet());
    }
}
