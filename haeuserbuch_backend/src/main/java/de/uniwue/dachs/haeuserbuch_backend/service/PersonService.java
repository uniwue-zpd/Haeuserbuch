package de.uniwue.dachs.haeuserbuch_backend.service;

import de.uniwue.dachs.haeuserbuch_backend.DTO.PersonDTO;
import de.uniwue.dachs.haeuserbuch_backend.DTO.PreviewDTO.PersonPreviewDTO;
import de.uniwue.dachs.haeuserbuch_backend.model.Person;
import de.uniwue.dachs.haeuserbuch_backend.model.PlaceCertainty;
import de.uniwue.dachs.haeuserbuch_backend.repository.PersonRepository;
import de.uniwue.dachs.haeuserbuch_backend.utils.Mappers.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class PersonService {
    private final PersonRepository personRepository;
    private final PersonMapper personMapper;
    private final BuildingMapper buildingMapper;
    private final PersonOriginMapper personOriginMapper;
    private final PersonOccupationMapper personOccupationMapper;
    private final PersonWeaponsMapper personWeaponsMapper;

    public PersonService(PersonRepository personRepository, PersonMapper personMapper, BuildingMapper buildingMapper, PersonOriginMapper personOriginMapper, PersonOccupationMapper personOccupationMapper, PersonWeaponsMapper personWeaponsMapper) {
        this.personRepository = personRepository;
        this.personMapper = personMapper;
        this.buildingMapper = buildingMapper;
        this.personOriginMapper = personOriginMapper;
        this.personOccupationMapper = personOccupationMapper;
        this.personWeaponsMapper = personWeaponsMapper;
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
     * GET filter persons by various attributes. At least one parameter must be provided, otherwise a bad request response is returned.
     * @param name matches full name, first name, last name or any of the alternative names (case-insensitive, partial match)
     * @param sex matches the sex field (case-insensitive, partial match)
     * @param occupation matches the occupation field (case-insensitive, partial match)
     * @param associatedBuildingId matches the ID of the associated building
     * @param isCitizen matches the isCitizen field
     * @param placeOfOriginId matches the ID of any place in the person's origin
     * @param originCertainty matches the certainty of the person's origin
     * @return {@link Set} of {@link PersonPreviewDTO} matching the provided criteria or an empty {@link Set} if no matches are found
     */
    public Set<PersonPreviewDTO> filterPersons(
            String name,
            String sex,
            String occupation,
            Long associatedBuildingId,
            Boolean isCitizen,
            Long placeOfOriginId,
            PlaceCertainty originCertainty
    ) {
        Specification<Person> spec = Specification.where(null);
        if (name != null && !name.isBlank()) {
            spec = spec.and(de.uniwue.dachs.haeuserbuch_backend.specification.PersonSpecification.hasName(name));
        }
        if (sex != null && !sex.isBlank()) {
            spec = spec.and(de.uniwue.dachs.haeuserbuch_backend.specification.PersonSpecification.hasSex(sex));
        }
        if (occupation != null && !occupation.isBlank()) {
            spec = spec.and(de.uniwue.dachs.haeuserbuch_backend.specification.PersonSpecification.hasOccupation(occupation));
        }
        if (associatedBuildingId != null) {
            spec = spec.and(de.uniwue.dachs.haeuserbuch_backend.specification.PersonSpecification.hasAssociatedBuildingId(associatedBuildingId));
        }
        if (isCitizen != null) {
            spec = spec.and(de.uniwue.dachs.haeuserbuch_backend.specification.PersonSpecification.isCitizen(isCitizen));
        }
        if (placeOfOriginId != null) {
            spec = spec.and(de.uniwue.dachs.haeuserbuch_backend.specification.PersonSpecification.hasPlaceOfOriginId(placeOfOriginId));
        }
        if (originCertainty != null) {
            spec = spec.and(de.uniwue.dachs.haeuserbuch_backend.specification.PersonSpecification.hasOriginCertainty(originCertainty));
        }
        Set<Person> results = new HashSet<>(personRepository.findAll(spec));
        return personMapper.PersonsToPreviewDTOs(results);
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
                    existingPerson.setOccupation(personOccupationMapper.DTOToPersonOccupation(updatedPerson.getOccupation()));
                    existingPerson.setAssociatedBuilding(buildingMapper.DTOToBuilding(updatedPerson.getAssociatedBuilding()));
                    existingPerson.setIsCitizen(updatedPerson.getIsCitizen());
                    existingPerson.setConfession(updatedPerson.getConfession());
                    existingPerson.setOrigin(personOriginMapper.DTOToPersonOrigin(updatedPerson.getOrigin()));
                    existingPerson.setWeapons(personWeaponsMapper.DTOToPersonWeapons(updatedPerson.getWeapons()));
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
