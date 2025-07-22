package de.uniwue.dachs.haeuserbuch_backend.service;

import de.uniwue.dachs.haeuserbuch_backend.DTO.CitizenshipDTO;
import de.uniwue.dachs.haeuserbuch_backend.model.*;
import de.uniwue.dachs.haeuserbuch_backend.repository.CitizenshipRepository;
import de.uniwue.dachs.haeuserbuch_backend.repository.PersonRepository;
import de.uniwue.dachs.haeuserbuch_backend.repository.PlaceRepository;
import de.uniwue.dachs.haeuserbuch_backend.repository.SourceRepository;
import de.uniwue.dachs.haeuserbuch_backend.DTO.GeoJsonDTO.Feature;
import de.uniwue.dachs.haeuserbuch_backend.utils.Mappers.PlaceMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CitizenshipService {
    private final CitizenshipRepository citizenshipRepository;
    private final PersonRepository personRepository;
    private final SourceRepository sourceRepository;
    private final PlaceRepository placeRepository;
    private final PlaceMapper placeMapper;

    public CitizenshipService(CitizenshipRepository citizenshipRepository,
                              PersonRepository personRepository,
                              SourceRepository sourceRepository,
                              PlaceRepository placeRepository, PlaceMapper placeMapper) {
        this.citizenshipRepository = citizenshipRepository;
        this.personRepository = personRepository;
        this.sourceRepository = sourceRepository;
        this.placeRepository = placeRepository;
        this.placeMapper = placeMapper;
    }

    // GET all citizenships
    public List<CitizenshipDTO> getAllCitizenships() {
        List<Citizenship> citizenships = citizenshipRepository.findAll();
        List<CitizenshipDTO> citizenshipDTOs = new ArrayList<>();
        citizenships.forEach(citizenship ->
                citizenshipDTOs.add(CitizenshipToDto(citizenship))
        );
        return citizenshipDTOs;
    }

    public Optional<CitizenshipDTO> getCitizenshipById(Long id) {
        return citizenshipRepository.findById(id)
                .map(this::CitizenshipToDto);
    }

    // POST
    @Transactional
    public void createCitizenship(CitizenshipDTO citizenshipDTO) {
        Citizenship citizenship = DtoToCitizenship(citizenshipDTO);
        citizenshipRepository.save(citizenship);
    }

    // DELETE
    @Transactional
    public void deleteCitizenship(Long id) {
        if (!citizenshipRepository.existsById(id)) {
            throw new RuntimeException("Citizenship with id '" + id + "' does not exist");
        }
        citizenshipRepository.deleteById(id);
    }

    // Helper methods
    private Citizenship DtoToCitizenship(CitizenshipDTO citizenshipDTO) {
        Citizenship citizenship = new Citizenship();
        citizenship.setPerson(getOrSavePerson(citizenshipDTO.getPerson()));
        citizenship.setSource(getOrSaveSource(citizenshipDTO.getSource()));
        citizenship.setPlace(getPlace(citizenshipDTO.getPlace()));
        citizenship.setNumber(citizenshipDTO.getNumber());
        citizenship.setDate(citizenshipDTO.getDate());
        citizenship.setEntryText(citizenshipDTO.getEntryText());
        citizenship.setAddendum(citizenshipDTO.getAddendum());
        citizenship.setNotes(citizenshipDTO.getNotes());
        return citizenship;
    }

    private CitizenshipDTO CitizenshipToDto(Citizenship citizenship) {
        CitizenshipDTO citizenshipDTO = new CitizenshipDTO();
        citizenshipDTO.setId(citizenship.getId());
        citizenshipDTO.setPerson(citizenship.getPerson());
        citizenshipDTO.setSource(citizenship.getSource());
        citizenshipDTO.setPlace(citizenship.getPlace() != null
                ? placeMapper.PlaceToFeature(citizenship.getPlace())
                : null);
        citizenshipDTO.setNumber(citizenship.getNumber());
        citizenshipDTO.setDate(citizenship.getDate());
        citizenshipDTO.setEntryText(citizenship.getEntryText());
        citizenshipDTO.setAddendum(citizenship.getAddendum());
        citizenshipDTO.setNotes(citizenship.getNotes());
        return citizenshipDTO;
    }

    private Person getOrSavePerson(Person person) {
        if (person.getId() != null) {
            return personRepository.findById(person.getId()).orElse(null);
        }
        return personRepository.save(person);
    }

    private Source getOrSaveSource(Source source) {
        if (source.getId() != null) {
            return sourceRepository.findById(source.getId()).orElse(null);
        }
        return sourceRepository.save(source);
    }

    private Place getPlace(Feature feature) {
        if (feature != null) {
            return placeRepository.findById(feature.getId()).orElse(null);
        }
        return null;
    }
}

// TODO: PUT
