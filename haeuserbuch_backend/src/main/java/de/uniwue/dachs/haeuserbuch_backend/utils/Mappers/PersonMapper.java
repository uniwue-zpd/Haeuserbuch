package de.uniwue.dachs.haeuserbuch_backend.utils.Mappers;

import de.uniwue.dachs.haeuserbuch_backend.DTO.PersonDTO;
import de.uniwue.dachs.haeuserbuch_backend.model.Person;
import org.springframework.stereotype.Component;

@Component
public class PersonMapper {
    public Person PersonDTOToPerson(PersonDTO personDTO) {
        Person person = new Person();
        person.setFirstName(personDTO.getFirstName());
        person.setLastName(personDTO.getLastName());
        person.setFullName(personDTO.getFullName());
        person.setSex(personDTO.getSex());
        person.setOccupation(personDTO.getOccupation());
        person.setOccupationCategory(personDTO.getOccupationCategory());
        person.setIsCitizen(personDTO.getIsCitizen());
        person.setConfession(personDTO.getConfession());
        return person;
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
}
