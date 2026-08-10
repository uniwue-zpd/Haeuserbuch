package de.uniwue.dachs.haeuserbuch_backend.service;

import de.uniwue.dachs.haeuserbuch_backend.DTO.PersonDTO;
import de.uniwue.dachs.haeuserbuch_backend.DTO.PreviewDTO.PersonPreviewDTO;
import de.uniwue.dachs.haeuserbuch_backend.model.Person;
import de.uniwue.dachs.haeuserbuch_backend.model.PlaceCertainty;
import de.uniwue.dachs.haeuserbuch_backend.model.Weaponry;
import de.uniwue.dachs.haeuserbuch_backend.repository.PersonRepository;
import de.uniwue.dachs.haeuserbuch_backend.specification.PersonSpecification;
import de.uniwue.dachs.haeuserbuch_backend.utils.Mappers.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PersonService {
    private final PersonRepository personRepository;
    private final PersonMapper personMapper;
    private final BuildingMapper buildingMapper;
    private final PersonOriginMapper personOriginMapper;
    private final PersonJobMapper personJobMapper;
    private final PersonReligionMapper personReligionMapper;
    private final WeaponryMapper weaponryMapper;

    public PersonService(PersonRepository personRepository, PersonMapper personMapper, BuildingMapper buildingMapper, PersonOriginMapper personOriginMapper, PersonJobMapper personJobMapper, PersonReligionMapper personReligionMapper, WeaponryMapper weaponryMapper) {
        this.personRepository = personRepository;
        this.personMapper = personMapper;
        this.buildingMapper = buildingMapper;
        this.personOriginMapper = personOriginMapper;
        this.personJobMapper = personJobMapper;
        this.personReligionMapper = personReligionMapper;
        this.weaponryMapper = weaponryMapper;
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
     * GET paged persons
     * @param pageable pagination and sorting information.
     * @param name Name of the person.
     * @param sex Sex of the person.
     * @param job Job of the person (either the original text or the category name can be used).
     * @param jobId ID of the job category.
     * @param associatedBuilding Building associated with the person (can be searched by building's ID, e.g. "IV/18").
     * @param associatedBuildingId ID of the associated building.
     * @param isCitizen Whether the person is a citizen or not.
     * @param placeOfOrigin Place of origin of the person (can be searched by original text).
     * @param placeOfOriginId ID of the place of origin.
     * @param originCertainty Certainty of the place of origin information.
     * @param religion Religion of the person (can be searched by original text or category name).
     * @param religionId ID of the religion category.
     * @param weapon Weapon associated with the person (can be searched by original text or category name).
     * @param weaponId ID of the weapon category.
     * @return {@link Page} of {@link PersonDTO}
     */
    public Page<PersonDTO> getPagedPeople(
            Pageable pageable,
            String name,
            String sex,
            String job,
            Long jobId,
            String associatedBuilding,
            Long associatedBuildingId,
            Boolean isCitizen,
            String placeOfOrigin,
            Long placeOfOriginId,
            PlaceCertainty originCertainty,
            String religion,
            Long religionId,
            String weapon,
            Long weaponId
    ) {
        Specification<Person> spec = Specification.where(null);
        if (name != null) spec = spec.and(PersonSpecification.hasName(name));
        if (sex != null && !sex.isBlank()) spec = spec.and(PersonSpecification.hasSex(sex));
        if (job != null && !job.isBlank()) spec = spec.and(PersonSpecification.hasJob(job));
        if (jobId != null) spec = spec.and(PersonSpecification.hasJobId(jobId));
        if (associatedBuilding != null && !associatedBuilding.isBlank()) spec = spec.and(PersonSpecification.hasAssociatedBuilding(associatedBuilding));
        if (associatedBuildingId != null) spec = spec.and(PersonSpecification.hasAssociatedBuildingId(associatedBuildingId));
        if (isCitizen != null) spec = spec.and(PersonSpecification.isCitizen(isCitizen));
        if (placeOfOrigin != null && !placeOfOrigin.isBlank()) spec = spec.and(PersonSpecification.hasPlaceOfOrigin(placeOfOrigin));
        if (placeOfOriginId != null) spec = spec.and(PersonSpecification.hasPlaceOfOriginId(placeOfOriginId));
        if (originCertainty != null) spec  = spec.and(PersonSpecification.hasOriginCertainty(originCertainty));
        if (religion != null && !religion.isBlank()) spec = spec.and(PersonSpecification.hasReligion(religion));
        if (religionId != null) spec = spec.and(PersonSpecification.hasReligionId(religionId));
        if (weapon != null && !weapon.isBlank()) spec = spec.and(PersonSpecification.hasWeapon(weapon));
        if (weaponId != null) spec = spec.and(PersonSpecification.hasWeaponId(weaponId));
        Page<Person> persons = personRepository.findAll(spec, pageable);
        return persons.map(personMapper::PersonToDTO);
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
     * @param name Name of the person.
     * @param sex Sex of the person.
     * @param job Job of the person (either the original text or the category name can be used).
     * @param jobId ID of the job category.
     * @param associatedBuilding Building associated with the person (can be searched by building's ID, e.g. "IV/18").
     * @param associatedBuildingId ID of the associated building.
     * @param isCitizen Whether the person is a citizen or not.
     * @param placeOfOrigin Place of origin of the person (can be searched by original text).
     * @param placeOfOriginId ID of the place of origin.
     * @param originCertainty Certainty of the place of origin information.
     * @param religion Religion of the person (can be searched by original text or category name).
     * @param religionId ID of the religion category.
     * @param weapon Weapon associated with the person (can be searched by original text or category name).
     * @param weaponId ID of the weapon category.
     * @return a {@link Set} of {@link PersonPreviewDTO} matching the provided filters.
     */
    public Set<PersonPreviewDTO> filterPersons(
            String name,
            String sex,
            String job,
            Long jobId,
            String associatedBuilding,
            Long associatedBuildingId,
            Boolean isCitizen,
            String placeOfOrigin,
            Long placeOfOriginId,
            PlaceCertainty originCertainty,
            String religion,
            Long religionId,
            String weapon,
            Long weaponId
    ) {
        Specification<Person> spec = Specification.where(null);
        if (name != null && !name.isBlank()) spec = spec.and(PersonSpecification.hasName(name));
        if (sex != null && !sex.isBlank()) spec = spec.and(PersonSpecification.hasSex(sex));
        if (job != null && !job.isBlank()) spec = spec.and(PersonSpecification.hasJob(job));
        if (jobId != null) spec = spec.and(PersonSpecification.hasJobId(jobId));
        if (associatedBuilding != null && !associatedBuilding.isBlank()) spec = spec.and(PersonSpecification.hasAssociatedBuilding(associatedBuilding));
        if (associatedBuildingId != null) spec = spec.and(PersonSpecification.hasAssociatedBuildingId(associatedBuildingId));
        if (isCitizen != null) spec = spec.and(PersonSpecification.isCitizen(isCitizen));
        if (placeOfOrigin != null && !placeOfOrigin.isBlank()) spec = spec.and(PersonSpecification.hasPlaceOfOrigin(placeOfOrigin));
        if (placeOfOriginId != null) spec = spec.and(PersonSpecification.hasPlaceOfOriginId(placeOfOriginId));
        if (originCertainty != null) spec = spec.and(PersonSpecification.hasOriginCertainty(originCertainty));
        if (religion != null && !religion.isBlank()) spec = spec.and(PersonSpecification.hasReligion(religion));
        if (religionId != null) spec = spec.and(PersonSpecification.hasReligionId(religionId));
        if (weapon != null && !weapon.isBlank()) spec = spec.and(PersonSpecification.hasWeapon(weapon));
        if (weaponId != null) spec = spec.and(PersonSpecification.hasWeaponId(weaponId));
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
                    existingPerson.setAssociatedBuilding(buildingMapper.DTOToBuilding(updatedPerson.getAssociatedBuilding()));
                    existingPerson.setIsCitizen(updatedPerson.getIsCitizen());
                    existingPerson.setOrigin(personOriginMapper.DTOToPersonOrigin(updatedPerson.getOrigin()));
                    existingPerson.setJob(personJobMapper.DTOToPersonJob(updatedPerson.getJob()));
                    existingPerson.setReligion(personReligionMapper.DTOToPersonReligion(updatedPerson.getReligion()));
                    Set<Weaponry> newWeaponry = weaponryMapper.DTOsToWeaponries(updatedPerson.getWeapons());
                    existingPerson.getWeapons().clear();
                    existingPerson.getWeapons().addAll(newWeaponry);
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

    /**
     * GET Allows searching for people by their names and alternative spellings of their names
     * @param query to be used
     * @return a {@link List} of {@link PersonPreviewDTO} matching the query
     */
    public List<PersonPreviewDTO> searchPeople(String query) {
        return personRepository.searchPeople(query).stream()
                .map(personMapper::PersonToPreviewDTO)
                .filter(Objects::nonNull)
                .toList();
    }
}
