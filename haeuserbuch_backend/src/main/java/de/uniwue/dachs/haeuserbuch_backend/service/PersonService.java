package de.uniwue.dachs.haeuserbuch_backend.service;

import de.uniwue.dachs.haeuserbuch_backend.model.Person;
import de.uniwue.dachs.haeuserbuch_backend.repository.PersonRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PersonService {
    private final PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    // Get all persons
    public List<Person> getAllPersons() {
        return personRepository.findAll();
    }

    // Get person by ID
    public Optional<Person> getPersonById(Long id) {
        return personRepository.findById(id);
    }

    // POST create a new person
    @Transactional
    public Person createPerson(Person person) {
        return personRepository.save(person);
    }

    // PUT update an existing person
    @Transactional
    public Person updatePerson(Long id, Person updatedPerson) {
        return personRepository.findById(id)
                .map(existingPerson -> {
                    existingPerson.setFirstName(updatedPerson.getFirstName());
                    existingPerson.setLastName(updatedPerson.getLastName());
                    existingPerson.setTopographicSurname(updatedPerson.getTopographicSurname());
                    existingPerson.setFullName(updatedPerson.getFullName());
                    existingPerson.setOccupation(updatedPerson.getOccupation());
                    existingPerson.setOccupationCategory(updatedPerson.getOccupationCategory());
                    existingPerson.setIsCitizen(updatedPerson.getIsCitizen());
                    existingPerson.setConfession(updatedPerson.getConfession());
                    existingPerson.setInternalNotes(updatedPerson.getInternalNotes());
                    existingPerson.setGeneralNotes(updatedPerson.getGeneralNotes());
                    return personRepository.save(existingPerson);
                })
                .orElse(null);
    }

    // DELETE a person by ID
    @Transactional
    public void deletePerson(Long id) {
        if (!personRepository.existsById(id)) {
            throw new IllegalArgumentException("Source with ID " + id + " does not exist.");
        }
        personRepository.deleteById(id);
    }
}
