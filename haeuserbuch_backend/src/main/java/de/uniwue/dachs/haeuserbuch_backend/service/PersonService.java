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
                    existingPerson.setFirst_name(updatedPerson.getFirst_name());
                    existingPerson.setLast_name(updatedPerson.getLast_name());
                    existingPerson.setTopographic_surname(updatedPerson.getTopographic_surname());
                    existingPerson.setFull_name(updatedPerson.getFull_name());
                    existingPerson.setOccupation(updatedPerson.getOccupation());
                    existingPerson.setIs_citizen(updatedPerson.getIs_citizen());
                    existingPerson.setConfession(updatedPerson.getConfession());
                    existingPerson.setNotes(updatedPerson.getNotes());
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
