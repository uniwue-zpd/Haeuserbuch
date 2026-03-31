package de.uniwue.dachs.haeuserbuch_backend.utils.Mappers;

import de.uniwue.dachs.haeuserbuch_backend.DTO.PersonDTO;
import de.uniwue.dachs.haeuserbuch_backend.DTO.PreviewDTO.PersonPreviewDTO;
import de.uniwue.dachs.haeuserbuch_backend.model.Person;
import de.uniwue.dachs.haeuserbuch_backend.repository.PersonRepository;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class PersonMapper {
    private final PersonRepository personRepository;
    private final BuildingMapper buildingMapper;
    private final PersonOriginMapper personOriginMapper;
    private final PersonOccupationMapper personOccupationMapper;
    private final PersonReligionMapper personReligionMapper;
    private final WeaponryMapper weaponryMapper;

    public PersonMapper(PersonRepository personRepository, BuildingMapper buildingMapper, PersonOriginMapper personOriginMapper, PersonOccupationMapper personOccupationMapper, PersonReligionMapper personReligionMapper, PersonWeaponryMapper personWeaponryMapper) {
        this.personRepository = personRepository;
        this.buildingMapper = buildingMapper;
        this.personOriginMapper = personOriginMapper;
        this.personOccupationMapper = personOccupationMapper;
        this.personReligionMapper = personReligionMapper;
        this.weaponryMapper = weaponryMapper;
    }

    public Person DTOToPerson(PersonDTO personDTO) {
        if (personDTO == null) return null;
        Person person = new Person();
        person.setFirstName(personDTO.getFirstName());
        person.setLastName(personDTO.getLastName());
        person.setFullName(personDTO.getFullName());
        person.setAltNames(personDTO.getAltNames());
        person.setSex(personDTO.getSex());
        person.setAssociatedBuilding(buildingMapper.DTOToBuilding(personDTO.getAssociatedBuilding()));
        person.setIsCitizen(personDTO.getIsCitizen());
        person.setOrigin(personOriginMapper.DTOToPersonOrigin(personDTO.getOrigin()));
        person.setOccupation(personOccupationMapper.DTOToPersonOccupation(personDTO.getOccupation()));
        person.setReligion(personReligionMapper.DTOToPersonReligion(personDTO.getReligion()));
        person.setWeapons(weaponryMapper.DTOsToWeaponries(personDTO.getWeapons()));
        person.setInternalNotes(personDTO.getInternalNotes());
        person.setGeneralNotes(personDTO.getGeneralNotes());
        return person;
    }

    public Set<Person> DTOsToPersons(Set<PersonDTO> personDTOs) {
        return personDTOs.stream().map(this::DTOToPerson).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    public PersonDTO PersonToDTO(Person person) {
        PersonDTO personDTO = new PersonDTO();
        personDTO.setId(person.getId());
        personDTO.setFirstName(person.getFirstName());
        personDTO.setLastName(person.getLastName());
        personDTO.setFullName(person.getFullName());
        personDTO.setAltNames(person.getAltNames());
        personDTO.setSex(person.getSex());
        personDTO.setAssociatedBuilding(buildingMapper.buildingToDTO(person.getAssociatedBuilding()));
        personDTO.setIsCitizen(person.getIsCitizen());
        personDTO.setOrigin(personOriginMapper.PersonOriginToDTO(person.getOrigin()));
        personDTO.setOccupation(personOccupationMapper.PersonOccupationToDTO(person.getOccupation()));
        personDTO.setReligion(personReligionMapper.PersonReligionToDTO(person.getReligion()));
        personDTO.setWeapons(weaponryMapper.WeaponriesToDTOs(person.getWeapons()));
        personDTO.setInternalNotes(person.getInternalNotes());
        personDTO.setGeneralNotes(person.getGeneralNotes());
        personDTO.setCreatedDate(person.getCreatedDate());
        personDTO.setLastModifiedDate(person.getLastModifiedDate());
        personDTO.setCreatedBy(person.getCreatedBy());
        personDTO.setLastModifiedBy(person.getLastModifiedBy());
        return personDTO;
    }

    public List<PersonDTO> PersonsToDTOs(List<Person> persons) {
        return persons.stream()
                .map(this::PersonToDTO)
                .sorted(Comparator.comparing(PersonDTO::getId))
                .collect(Collectors.toList());
    }

    public Person PreviewDTOToPerson(PersonPreviewDTO previewDTO) {
        if (previewDTO == null || previewDTO.getId() == null) return null;
        return personRepository.findById(previewDTO.getId()).orElse(null);
    }

    public Set<Person> PreviewDTOsToPersons(Set<PersonPreviewDTO> previewDTOs) {
        return previewDTOs.stream().map(this::PreviewDTOToPerson).filter(Objects::nonNull).collect(Collectors.toSet());
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
