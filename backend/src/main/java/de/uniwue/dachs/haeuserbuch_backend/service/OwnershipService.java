package de.uniwue.dachs.haeuserbuch_backend.service;

import de.uniwue.dachs.haeuserbuch_backend.DTO.GeoJsonDTO.Feature;
import de.uniwue.dachs.haeuserbuch_backend.DTO.OwnershipDTO;
import de.uniwue.dachs.haeuserbuch_backend.model.Building;
import de.uniwue.dachs.haeuserbuch_backend.model.Ownership;
import de.uniwue.dachs.haeuserbuch_backend.model.Person;
import de.uniwue.dachs.haeuserbuch_backend.model.Source;
import de.uniwue.dachs.haeuserbuch_backend.repository.BuildingRepository;
import de.uniwue.dachs.haeuserbuch_backend.repository.OwnershipRepository;
import de.uniwue.dachs.haeuserbuch_backend.repository.PersonRepository;
import de.uniwue.dachs.haeuserbuch_backend.repository.SourceRepository;
import de.uniwue.dachs.haeuserbuch_backend.utils.Mappers.BuildingMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class OwnershipService {
    private final OwnershipRepository ownershipRepository;
    private final BuildingRepository buildingRepository;
    private final SourceRepository sourceRepository;
    private final BuildingMapper buildingMapper;
    private final PersonRepository personRepository;

    public OwnershipService(OwnershipRepository ownershipRepository,
                            BuildingRepository buildingRepository,
                            SourceRepository sourceRepository,
                            BuildingMapper buildingMapper,
                            PersonRepository personRepository) {
        this.ownershipRepository = ownershipRepository;
        this.buildingRepository = buildingRepository;
        this.sourceRepository = sourceRepository;
        this.buildingMapper = buildingMapper;
        this.personRepository = personRepository;
    }

    // GET all ownerships
    @Cacheable("ownerships")
    public List<OwnershipDTO> getAllOwnerships() {
        List<Ownership> ownerships = ownershipRepository.findAll();
        List<OwnershipDTO> ownershipDTOs = new ArrayList<>();
        ownerships.forEach(ownership ->
                ownershipDTOs.add(OwnershipToDto(ownership))
        );
        return ownershipDTOs;
    }

    // GET ownership by ID
    @Cacheable(value = "ownerships", key = "#id")
    public Optional<OwnershipDTO> getOwnershipById(Long id) {
        return ownershipRepository.findById(id)
                .map(this::OwnershipToDto);
    }

    // POST create new ownership
    @Transactional
    @CacheEvict(value = "ownerships", allEntries = true)
    public void createOwnership(OwnershipDTO ownershipDTO) {
        Ownership ownership = DtoToOwnership(ownershipDTO);
        ownershipRepository.save(ownership);
    }

    // PUT update existing ownership
    @Transactional
    @CachePut(value = "ownerships", key = "#id")
    public void updateOwnership(Long id, OwnershipDTO ownershipDTO) {
        ownershipRepository.findById(id)
                .map(entity -> {
                    entity.setType(ownershipDTO.getType());
                    entity.setDate(ownershipDTO.getDate());
                    entity.setPrice(ownershipDTO.getPrice());
                    entity.setOwner(getPerson(ownershipDTO.getOwner()));
                    entity.setSeller(getPerson(ownershipDTO.getSeller()));
                    entity.setBuildings(getBuildings(ownershipDTO.getBuildings()));
                    entity.setSource(getSource(ownershipDTO.getSource()));
                    entity.setEntryText(ownershipDTO.getEntryText());
                    entity.setInternalNotes(ownershipDTO.getInternalNotes());
                    entity.setGeneralNotes(ownershipDTO.getGeneralNotes());
                    return ownershipRepository.save(entity);
                }).orElseThrow(() -> new EntityNotFoundException("Ownership with id '" + id + "' does not exist"));
    }

    // DELETE ownership by ID
    @Transactional
    @CacheEvict(value = "ownerships", key = "#id")
    public void deleteOwnership(Long id) {
        if (!ownershipRepository.existsById(id)) {
            throw new EntityNotFoundException("Ownership with id '" + id + "' does not exist");
        }
        ownershipRepository.deleteById(id);
    }

    // Helper methods
    private Ownership DtoToOwnership(OwnershipDTO ownershipDTO) {
        Ownership ownership = new Ownership();
        ownership.setType(ownershipDTO.getType());
        ownership.setDate(ownershipDTO.getDate());
        ownership.setPrice(ownershipDTO.getPrice());
        ownership.setOwner(getPerson(ownershipDTO.getOwner()));
        ownership.setSeller(getPerson(ownershipDTO.getSeller()));
        ownership.setBuildings(getBuildings(ownershipDTO.getBuildings()));
        ownership.setSource(getSource(ownershipDTO.getSource()));
        ownership.setEntryText(ownershipDTO.getEntryText());
        ownership.setInternalNotes(ownershipDTO.getInternalNotes());
        ownership.setGeneralNotes(ownershipDTO.getGeneralNotes());
        return ownership;
    }

    private OwnershipDTO OwnershipToDto(Ownership ownership) {
        OwnershipDTO ownershipDTO = new OwnershipDTO();
        ownershipDTO.setId(ownership.getId());
        ownershipDTO.setType(ownership.getType());
        ownershipDTO.setDate(ownership.getDate());
        ownershipDTO.setPrice(ownership.getPrice());
        ownershipDTO.setOwner(ownership.getOwner());
        ownershipDTO.setSeller(ownership.getSeller());
        Set<Feature> buildings = new HashSet<>();
        ownership.getBuildings().forEach(building -> buildings.add(buildingMapper.BuildingToFeature(building)));
        ownershipDTO.setBuildings(buildings);
        ownershipDTO.setSource(ownership.getSource());
        ownershipDTO.setEntryText(ownership.getEntryText());
        ownershipDTO.setInternalNotes(ownership.getInternalNotes());
        ownershipDTO.setGeneralNotes(ownership.getGeneralNotes());
        return ownershipDTO;
    }

    private Set<Building> getBuildings(Set<Feature> features) {
        if (features == null || features.isEmpty()) {
            return Set.of();
        }
        Set<Building> buildings = new HashSet<>();
        features.forEach(feature -> {
            if (feature.getId() != null) {
                buildingRepository.findById(feature.getId()).ifPresent(buildings::add);
            }
        });
        return buildings;
    }

    private Person getPerson(Person person) {
        if (person == null || person.getId() == null) {
            return null;
        }
        return personRepository.findById(person.getId()).orElseThrow(
                () -> new EntityNotFoundException("Person with id '" + person.getId() + "' does not exist")
        );
    }

    private Source getSource(Source source) {
        if (source == null || source.getId() == null) {
            return null;
        }
        return sourceRepository.findById(source.getId()).orElseThrow(
                () -> new EntityNotFoundException("Source with id '" + source.getId() + "' does not exist")
        );
    }
}
