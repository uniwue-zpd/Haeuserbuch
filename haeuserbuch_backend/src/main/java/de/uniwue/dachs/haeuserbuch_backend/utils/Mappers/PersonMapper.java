package de.uniwue.dachs.haeuserbuch_backend.utils.Mappers;

import de.uniwue.dachs.haeuserbuch_backend.DTO.PersonDTO;
import de.uniwue.dachs.haeuserbuch_backend.model.Person;
import de.uniwue.dachs.haeuserbuch_backend.repository.PersonRepository;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class PersonMapper {
    private final PersonRepository personRepository;

    public PersonMapper(PersonRepository personRepository) {
        this.personRepository = personRepository;
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
            person.setSex(personDTO.getSex());
            person.setOccupation(personDTO.getOccupation());
            person.setOccupationCategory(personDTO.getOccupationCategory());
            person.setIsCitizen(personDTO.getIsCitizen());
            person.setConfession(personDTO.getConfession());
            personRepository.save(person);
            return person;
        }
    }

    public Set<Person> PersonDTOsToPersons(Set<PersonDTO> personDTOs) {
        return personDTOs.stream().map(this::PersonDTOToPerson).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    public PersonDTO PersonToPersonDTO(Person person) {
        PersonDTO personDTO = new PersonDTO();
        personDTO.setId(person.getId());
        personDTO.setFirstName(person.getFirstName());
        personDTO.setLastName(person.getLastName());
        personDTO.setFullName(person.getFullName());
        personDTO.setSex(person.getSex());
        personDTO.setOccupation(person.getOccupation());
        personDTO.setOccupationCategory(person.getOccupationCategory());
        personDTO.setIsCitizen(person.getIsCitizen());
        personDTO.setConfession(person.getConfession());
        return personDTO;
    }

    public Set<PersonDTO> PersonsToPersonDTOs(Set<Person> persons) {
        return persons.stream().map(this::PersonToPersonDTO).collect(Collectors.toSet());
    }
}
