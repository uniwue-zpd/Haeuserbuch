package de.uniwue.dachs.haeuserbuch_backend.service;

import de.uniwue.dachs.haeuserbuch_backend.DTO.CitizenshipDTO;
import de.uniwue.dachs.haeuserbuch_backend.DTO.PlaceDTO;
import de.uniwue.dachs.haeuserbuch_backend.model.Citizenship;
import de.uniwue.dachs.haeuserbuch_backend.model.Person;
import de.uniwue.dachs.haeuserbuch_backend.model.Place;
import de.uniwue.dachs.haeuserbuch_backend.model.TownBook;
import de.uniwue.dachs.haeuserbuch_backend.repository.CitizenshipRepository;
import de.uniwue.dachs.haeuserbuch_backend.repository.PersonRepository;
import de.uniwue.dachs.haeuserbuch_backend.repository.PlaceRepository;
import de.uniwue.dachs.haeuserbuch_backend.repository.TownBookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static de.uniwue.dachs.haeuserbuch_backend.utils.PostGIS.GeometryUtils.convertPoint;
import static de.uniwue.dachs.haeuserbuch_backend.utils.PostGIS.GeometryUtils.createPoint;

@Service
public class CitizenshipService {
    private final CitizenshipRepository citizenshipRepository;
    private final PersonRepository personRepository;
    private final TownBookRepository townBookRepository;
    private final PlaceRepository placeRepository;

    public CitizenshipService(CitizenshipRepository citizenshipRepository,
                              PersonRepository personRepository,
                              TownBookRepository townBookRepository,
                              PlaceRepository placeRepository) {
        this.citizenshipRepository = citizenshipRepository;
        this.personRepository = personRepository;
        this.townBookRepository = townBookRepository;
        this.placeRepository = placeRepository;
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
        citizenship.setTownbook(getOrSaveTownBook(citizenshipDTO.getTownBook()));
        citizenship.setPlace(getOrSavePlaceDTO(citizenshipDTO.getPlace()));
        citizenship.setNumber(citizenshipDTO.getNumber());
        citizenship.setDate(citizenshipDTO.getDate());
        citizenship.setEntry_text(citizenshipDTO.getEntry_text());
        citizenship.setAddendum(citizenshipDTO.getAddendum());
        return citizenship;
    }

    private CitizenshipDTO CitizenshipToDto(Citizenship citizenship) {
        CitizenshipDTO citizenshipDTO = new CitizenshipDTO();
        citizenshipDTO.setId(citizenship.getId());
        citizenshipDTO.setPerson(citizenship.getPerson());
        citizenshipDTO.setTownBook(citizenship.getTownbook());
        if (citizenship.getPlace() != null) {
            citizenshipDTO.setPlace(PlaceToDto(citizenship.getPlace()));
        }
        citizenshipDTO.setNumber(citizenship.getNumber());
        citizenshipDTO.setDate(citizenship.getDate());
        citizenshipDTO.setEntry_text(citizenship.getEntry_text());
        citizenshipDTO.setAddendum(citizenship.getAddendum());
        return citizenshipDTO;
    }

    private PlaceDTO PlaceToDto(Place place) {
        PlaceDTO placeDTO = new PlaceDTO();
        placeDTO.setId(place.getId());
        placeDTO.setReal_name(place.getReal_name());
        placeDTO.setAlt_names(place.getAlt_names());
        placeDTO.setCoordinates(convertPoint(place.getCoordinates()));
        return placeDTO;
    }

    private Person getOrSavePerson(Person person) {
        if (person.getId() != null) {
            return personRepository.findById(person.getId()).orElse(null);
        }
        return personRepository.save(person);
    }

    private TownBook getOrSaveTownBook(TownBook townBook) {
        if (townBook.getId() != null) {
            return townBookRepository.findById(townBook.getId()).orElse(null);
        }
        return townBookRepository.save(townBook);
    }

    private Place getOrSavePlaceDTO(PlaceDTO placeDTO) {
        if (placeDTO == null) {
            return null;
        }
        if (placeDTO.getId() != null) {
            return placeRepository.findById(placeDTO.getId()).orElse(null);
        }
        Place place = new Place();
        place.setReal_name(placeDTO.getReal_name());
        place.setAlt_names(placeDTO.getAlt_names());
        place.setCoordinates(createPoint(placeDTO.getCoordinates()));
        return placeRepository.save(place);
    }
}

// TODO: PUT/PATCH
