package de.uniwue.dachs.haeuserbuch_backend.service;

import de.uniwue.dachs.haeuserbuch_backend.DTO.PersonDTO;
import de.uniwue.dachs.haeuserbuch_backend.model.Person;
import de.uniwue.dachs.haeuserbuch_backend.repository.PersonRepository;
import de.uniwue.dachs.haeuserbuch_backend.utils.Mappers.BuildingMapper;
import de.uniwue.dachs.haeuserbuch_backend.utils.Mappers.PersonMapper;
import de.uniwue.dachs.haeuserbuch_backend.utils.Mappers.PersonOriginMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PersonService {
    private final PersonRepository personRepository;
    private final PersonMapper personMapper;
    private final BuildingMapper buildingMapper;
    private final PersonOriginMapper personOriginMapper;

    public PersonService(PersonRepository personRepository, PersonMapper personMapper, BuildingMapper buildingMapper, PersonOriginMapper personOriginMapper) {
        this.personRepository = personRepository;
        this.personMapper = personMapper;
        this.buildingMapper = buildingMapper;
        this.personOriginMapper = personOriginMapper;
    }

    /**
     * GET all persons
     * @return {@link List} of {@link PersonDTO}
     */
    @Cacheable("persons")
    public List<PersonDTO> getAllPersons() {
        return personMapper.PersonsToDTOs(personRepository.findAll());
    }

    /**
     * GET person by ID
     * @param id of the person
     * @return {@link Optional} of {@link PersonDTO}
     */
    @Cacheable(value = "persons", key = "#id")
    public Optional<PersonDTO> getPersonById(Long id) {
        return personRepository.findById(id)
                .map(personMapper::PersonToDTO);
    }

    /**
     * POST create a new person
     * @param personDTO to create
     * @return created {@link PersonDTO}
     */
    @Transactional
    @CacheEvict(value = "persons", allEntries = true)
    public PersonDTO createPerson(PersonDTO personDTO) {
        return personMapper.PersonToDTO(
                personRepository.save(personMapper.DTOToPerson(personDTO))
        );
    }

    /**
     * PUT update an existing person
     * @param id of the person to update
     * @param updatedPerson with updated fields
     * @return updated {@link PersonDTO}
     */
    @Transactional
    @CacheEvict(value = "persons", allEntries = true)
    public PersonDTO updatePerson(Long id, PersonDTO updatedPerson) {
        return personRepository.findById(id)
                .map(existingPerson -> {
                    existingPerson.setFirstName(updatedPerson.getFirstName());
                    existingPerson.setLastName(updatedPerson.getLastName());
                    existingPerson.setFullName(updatedPerson.getFullName());
                    existingPerson.setAltNames(updatedPerson.getAltNames());
                    existingPerson.setSex(updatedPerson.getSex());
                    existingPerson.setOccupation(updatedPerson.getOccupation());
                    existingPerson.setOccupationCategory(updatedPerson.getOccupationCategory());
                    existingPerson.setAssociatedBuilding(buildingMapper.DTOToBuilding(updatedPerson.getAssociatedBuilding()));
                    existingPerson.setIsCitizen(updatedPerson.getIsCitizen());
                    existingPerson.setConfession(updatedPerson.getConfession());
                    existingPerson.setOrigin(personOriginMapper.DTOToPersonOrigin(updatedPerson.getOrigin()));
                    existingPerson.setInternalNotes(updatedPerson.getInternalNotes());
                    existingPerson.setGeneralNotes(updatedPerson.getGeneralNotes());
                    Person saved = personRepository.save(existingPerson);
                    return personMapper.PersonToDTO(saved);
                })
                .orElseThrow(() -> new EntityNotFoundException("Person with ID " + id + " does not exist."));
    }

    /**
     * DELETE a person by ID
     * @param id of the person to delete
     */
    @Transactional
    @CacheEvict(value = "persons", allEntries = true)
    public void deletePerson(Long id) {
        if (!personRepository.existsById(id)) throw new EntityNotFoundException("Person with ID " + id + " does not exist.");
        personRepository.deleteById(id);
    }
}
