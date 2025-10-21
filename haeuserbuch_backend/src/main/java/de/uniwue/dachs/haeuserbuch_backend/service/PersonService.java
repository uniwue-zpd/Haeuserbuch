package de.uniwue.dachs.haeuserbuch_backend.service;

import de.uniwue.dachs.haeuserbuch_backend.DTO.PersonDTO;
import de.uniwue.dachs.haeuserbuch_backend.model.Person;
import de.uniwue.dachs.haeuserbuch_backend.repository.PersonRepository;
import de.uniwue.dachs.haeuserbuch_backend.utils.Mappers.PersonMapper;
import de.uniwue.dachs.haeuserbuch_backend.utils.Mappers.PlaceMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class PersonService {
    private final PersonRepository personRepository;
    private final PlaceMapper placeMapper;
    private final PersonMapper personMapper;

    public PersonService(PersonRepository personRepository, PlaceMapper placeMapper, PersonMapper personMapper) {
        this.personRepository = personRepository;
        this.placeMapper = placeMapper;
        this.personMapper = personMapper;
    }

    // Get all persons
    @Cacheable("persons")
    public List<PersonDTO> getAllPersons() {
        return personRepository.findAll()
                .stream()
                .map(personMapper::PersonToPersonDTO)
                .sorted(Comparator.comparing(PersonDTO::getId))
                .toList();
    }

    // Get person by ID
    @Cacheable(value = "persons", key = "#id")
    public Optional<PersonDTO> getPersonById(Long id) {
        return personRepository.findById(id)
                .map(personMapper::PersonToPersonDTO);
    }

    // POST create a new person
    @Transactional
    @CacheEvict(value = "persons", allEntries = true)
    public void createPerson(PersonDTO personDTO) {
        personMapper.PersonDTOToPerson(personDTO);
    }

    // PUT update an existing person
    @Transactional
    @CacheEvict(value = "persons", allEntries = true)
    public Person updatePerson(Long id, PersonDTO updatedPerson) {
        return personRepository.findById(id)
                .map(existingPerson -> {
                    existingPerson.setFirstName(updatedPerson.getFirstName());
                    existingPerson.setLastName(updatedPerson.getLastName());
                    existingPerson.setFullName(updatedPerson.getFullName());
                    existingPerson.setSex(updatedPerson.getSex());
                    existingPerson.setOccupation(updatedPerson.getOccupation());
                    existingPerson.setOccupationCategory(updatedPerson.getOccupationCategory());
                    existingPerson.setIsCitizen(updatedPerson.getIsCitizen());
                    existingPerson.setConfession(updatedPerson.getConfession());
                    existingPerson.setOrigin(placeMapper.PlaceDTOToPlace(updatedPerson.getOrigin()));
                    existingPerson.setInternalNotes(updatedPerson.getInternalNotes());
                    existingPerson.setGeneralNotes(updatedPerson.getGeneralNotes());
                    return personRepository.save(existingPerson);
                })
                .orElseThrow(() -> new EntityNotFoundException("Person with ID " + id + " does not exist."));
    }

    // DELETE a person by ID
    @Transactional
    @CacheEvict(value = "persons", key = "#id")
    public void deletePerson(Long id) {
        if (!personRepository.existsById(id)) {
            throw new EntityNotFoundException("Person with ID " + id + " does not exist.");
        }
        personRepository.deleteById(id);
    }
}
