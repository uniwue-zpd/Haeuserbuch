package de.uniwue.dachs.haeuserbuch_backend.service;

import de.uniwue.dachs.haeuserbuch_backend.DTO.CitizenshipDTO;
import de.uniwue.dachs.haeuserbuch_backend.DTO.PersonDTO;
import de.uniwue.dachs.haeuserbuch_backend.DTO.SourceDTO;
import de.uniwue.dachs.haeuserbuch_backend.model.*;
import de.uniwue.dachs.haeuserbuch_backend.repository.CitizenshipRepository;
import de.uniwue.dachs.haeuserbuch_backend.repository.PersonRepository;
import de.uniwue.dachs.haeuserbuch_backend.repository.PlaceRepository;
import de.uniwue.dachs.haeuserbuch_backend.repository.SourceRepository;
import de.uniwue.dachs.haeuserbuch_backend.DTO.GeoJsonDTO.Feature;
import de.uniwue.dachs.haeuserbuch_backend.utils.Mappers.PersonMapper;
import de.uniwue.dachs.haeuserbuch_backend.utils.Mappers.PlaceMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CitizenshipService {
    private final CitizenshipRepository citizenshipRepository;
    private final PersonRepository personRepository;
    private final SourceRepository sourceRepository;
    private final PlaceRepository placeRepository;
    private final PlaceMapper placeMapper;
    private final PersonMapper personMapper;

    public CitizenshipService(CitizenshipRepository citizenshipRepository,
                              PersonRepository personRepository,
                              SourceRepository sourceRepository,
                              PlaceRepository placeRepository, PlaceMapper placeMapper, PersonMapper personMapper) {
        this.citizenshipRepository = citizenshipRepository;
        this.personRepository = personRepository;
        this.sourceRepository = sourceRepository;
        this.placeRepository = placeRepository;
        this.placeMapper = placeMapper;
        this.personMapper = personMapper;
    }

    // GET all citizenships
    @Cacheable("citizenships")
    public List<CitizenshipDTO> getAllCitizenships() {
        List<Citizenship> citizenships = citizenshipRepository.findAll();
        List<CitizenshipDTO> citizenshipDTOs = new ArrayList<>();
        citizenships.forEach(citizenship ->
                citizenshipDTOs.add(CitizenshipToDto(citizenship))
        );
        return citizenshipDTOs;
    }

    // GET citizenship by ID
    @Cacheable(value = "citizenships", key = "#id")
    public Optional<CitizenshipDTO> getCitizenshipById(Long id) {
        return citizenshipRepository.findById(id)
                .map(this::CitizenshipToDto);
    }

    // POST
    @Transactional
    @CacheEvict(value = "citizenships", allEntries = true)
    public void createCitizenship(CitizenshipDTO citizenshipDTO) {
        Citizenship citizenship = DtoToCitizenship(citizenshipDTO);
        citizenshipRepository.save(citizenship);
    }

    // PUT
    @Transactional
    @CacheEvict(value = "citizenships", key = "#id")
    public void updateCitizenship(Long id, CitizenshipDTO updatedCitizenshipDTO) {
        citizenshipRepository.findById(id)
                .map(existingCitizenship -> {
                    existingCitizenship.setPersons(getOrSavePersons(updatedCitizenshipDTO.getPersons()));
                    existingCitizenship.setSource(getSource(updatedCitizenshipDTO.getSource()));
                    existingCitizenship.setPlace(getPlace(updatedCitizenshipDTO.getPlace()));
                    existingCitizenship.setNumber(updatedCitizenshipDTO.getNumber());
                    existingCitizenship.setDate(updatedCitizenshipDTO.getDate());
                    existingCitizenship.setEntryText(updatedCitizenshipDTO.getEntryText());
                    existingCitizenship.setAddendum(updatedCitizenshipDTO.getAddendum());
                    existingCitizenship.setInternalNotes(updatedCitizenshipDTO.getInternalNotes());
                    existingCitizenship.setGeneralNotes(updatedCitizenshipDTO.getGeneralNotes());
                    return citizenshipRepository.save(existingCitizenship);
                })
                .orElseThrow(() -> new EntityNotFoundException("Citizenship with id '" + id + "' does not exist"));
    }

    // DELETE
    @Transactional
    @CacheEvict(value = "citizenships", key = "#id")
    public void deleteCitizenship(Long id) {
        if (!citizenshipRepository.existsById(id)) {
            throw new RuntimeException("Citizenship with id '" + id + "' does not exist");
        }
        citizenshipRepository.deleteById(id);
    }

    // Helper methods
    private Citizenship DtoToCitizenship(CitizenshipDTO citizenshipDTO) {
        Citizenship citizenship = new Citizenship();
        citizenship.setPersons(getOrSavePersons(citizenshipDTO.getPersons()));
        citizenship.setSource(getSource(citizenshipDTO.getSource()));
        citizenship.setPlace(getPlace(citizenshipDTO.getPlace()));
        citizenship.setNumber(citizenshipDTO.getNumber());
        citizenship.setDate(citizenshipDTO.getDate());
        citizenship.setEntryText(citizenshipDTO.getEntryText());
        citizenship.setAddendum(citizenshipDTO.getAddendum());
        citizenship.setInternalNotes(citizenshipDTO.getInternalNotes());
        citizenship.setGeneralNotes(citizenshipDTO.getGeneralNotes());
        return citizenship;
    }

    private CitizenshipDTO CitizenshipToDto(Citizenship citizenship) {
        CitizenshipDTO citizenshipDTO = new CitizenshipDTO();
        citizenshipDTO.setId(citizenship.getId());
        citizenshipDTO.setPersons(getPersonDTOs(citizenship.getPersons()));
        citizenshipDTO.setSource(getSourceDTO(citizenship.getSource()));
        citizenshipDTO.setPlace(getPlaceFeature(citizenship.getPlace()));
        citizenshipDTO.setNumber(citizenship.getNumber());
        citizenshipDTO.setDate(citizenship.getDate());
        citizenshipDTO.setEntryText(citizenship.getEntryText());
        citizenshipDTO.setAddendum(citizenship.getAddendum());
        citizenshipDTO.setInternalNotes(citizenship.getInternalNotes());
        citizenshipDTO.setGeneralNotes(citizenship.getGeneralNotes());
        citizenshipDTO.setCreatedBy(citizenship.getCreatedBy());
        citizenshipDTO.setCreatedDate(citizenship.getCreatedDate());
        citizenshipDTO.setLastModifiedBy(citizenship.getLastModifiedBy());
        citizenshipDTO.setLastModifiedDate(citizenship.getLastModifiedDate());
        return citizenshipDTO;
    }

    // Persons
        // Get existing persons or save new ones
    private Set<Person> getOrSavePersons(Set<PersonDTO> personDTOs) {
        if (personDTOs == null || personDTOs.isEmpty()) {
            return new HashSet<>();
        }
        return personDTOs.stream()
                .map(person -> {
                    if (person.getId() != null) {
                        return personRepository.findById(person.getId())
                                .orElse(null);
                    } else {
                        Person newPerson = personMapper.PersonDTOToPerson(person);
                        return personRepository.save(newPerson);
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

        // Get PersonDTOs from Persons
    private Set<PersonDTO> getPersonDTOs(Set<Person> persons) {
        if (persons == null || persons.isEmpty()) {
            return new HashSet<>();
        }
        return persons.stream()
                .map(personMapper::PersonToPersonDTO)
                .collect(Collectors.toSet());
    }

    // Source
        // Get Source from SourceDTO
    private Source getSource(SourceDTO sourceDTO) {
        if (sourceDTO == null || sourceDTO.getId() == null) {
            return null;
        }
        return sourceRepository.findById(sourceDTO.getId()).orElse(null);
    }

        // Get SourceDTO from Source
    private SourceDTO getSourceDTO(Source source) {
        if (source == null) {
            return null;
        }
        SourceDTO sourceDTO = new SourceDTO();
        sourceDTO.setId(source.getId());
        sourceDTO.setTitle(source.getTitle());
        return sourceDTO;
    }

    // Place
        // Get Place from Feature
    private Place getPlace(Feature feature) {
        if (feature != null) {
            return placeRepository.findById(feature.getId()).orElse(null);
        }
        return null;
    }

        // Get Feature from Place
    private Feature getPlaceFeature(Place place) {
        if (place != null) {
            return placeMapper.PlaceToFeature(place);
        }
        return null;
    }
}
