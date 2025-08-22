package de.uniwue.dachs.haeuserbuch_backend.service;

import de.uniwue.dachs.haeuserbuch_backend.DTO.TaxDTO;
import de.uniwue.dachs.haeuserbuch_backend.model.Building;
import de.uniwue.dachs.haeuserbuch_backend.model.Person;
import de.uniwue.dachs.haeuserbuch_backend.model.Source;
import de.uniwue.dachs.haeuserbuch_backend.model.Tax;
import de.uniwue.dachs.haeuserbuch_backend.repository.BuildingRepository;
import de.uniwue.dachs.haeuserbuch_backend.repository.PersonRepository;
import de.uniwue.dachs.haeuserbuch_backend.repository.SourceRepository;
import de.uniwue.dachs.haeuserbuch_backend.repository.TaxRepository;
import de.uniwue.dachs.haeuserbuch_backend.DTO.GeoJsonDTO.BuildingProperties;
import de.uniwue.dachs.haeuserbuch_backend.DTO.GeoJsonDTO.Feature;
import de.uniwue.dachs.haeuserbuch_backend.utils.Mappers.BuildingMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TaxService {
    private final BuildingRepository buildingRepository;
    private final PersonRepository personRepository;
    private final SourceRepository sourceRepository;
    private final TaxRepository taxRepository;
    private final BuildingMapper buildingMapper;

    public TaxService(BuildingRepository buildingRepository,
                      PersonRepository personRepository,
                      SourceRepository sourceRepository,
                      TaxRepository taxRepository,
                      BuildingMapper buildingMapper) {
        this.buildingRepository = buildingRepository;
        this.personRepository = personRepository;
        this.sourceRepository = sourceRepository;
        this.taxRepository = taxRepository;
        this.buildingMapper = buildingMapper;
    }

    // GET
    @Cacheable("taxes")
    public List<TaxDTO> getAllTaxes() {
        List<Tax> taxes = taxRepository.findAll();
        List<TaxDTO> taxDTOs = new ArrayList<>();
        taxes.forEach(tax ->
                taxDTOs.add(taxToDto(tax))
        );
        return taxDTOs;
    }

    // GET
    @Cacheable(value = "taxes", key = "#id")
    public Optional<TaxDTO> getTaxById(Long id) {
        return taxRepository.findById(id)
                .map(this::taxToDto);
    }

    // POST
    @Transactional
    @CacheEvict(value = "taxes", allEntries = true)
    public void createTax(TaxDTO taxDTO) {
        Tax tax = DtoToTax(taxDTO);
        taxRepository.save(tax);
    }

    // PUT
    @Transactional
    @CachePut(value = "taxes", key = "#id")
    public void updateTax(Long id, TaxDTO taxDTO) {
        taxRepository.findById(id)
                .map(entity -> {
                    entity.setTaxNumber(taxDTO.getTaxNumber());
                    entity.setPlanNumber(taxDTO.getPlanNumber());
                    entity.setEntryText(taxDTO.getEntryText());
                    entity.setBuilding(getBuilding(taxDTO.getBuilding()));
                    entity.setPerson(getOrSavePerson(taxDTO.getPerson()));
                    entity.setSource(getOrSaveSource(taxDTO.getSource()));
                    entity.setInternalNotes(taxDTO.getInternalNotes());
                    entity.setGeneralNotes(taxDTO.getGeneralNotes());
                    return taxRepository.save(entity);
                })
                .orElseThrow(() -> new EntityNotFoundException("Tax with id " + id + " does not exist"));
    }

    // DELETE
    @Transactional
    @CacheEvict(value = "taxes", key = "#id")
    public void deleteTax(Long id) {
        if (!taxRepository.existsById(id)) {
            throw new IllegalArgumentException("Tax with id " + id + " does not exist");
        }
        taxRepository.deleteById(id);
    }

    // Helper methods
    private Tax DtoToTax(TaxDTO taxDTO) {
        Tax tax = new Tax();
        tax.setTaxNumber(taxDTO.getTaxNumber());
        tax.setPlanNumber(taxDTO.getPlanNumber());
        tax.setEntryText(taxDTO.getEntryText());
        tax.setBuilding(getBuilding(taxDTO.getBuilding()));
        tax.setPerson(getOrSavePerson(taxDTO.getPerson()));
        tax.setSource(getOrSaveSource(taxDTO.getSource()));
        tax.setInternalNotes(taxDTO.getInternalNotes());
        tax.setGeneralNotes(taxDTO.getGeneralNotes());
        return tax;
    }

    private TaxDTO taxToDto(Tax tax) {
        TaxDTO taxDTO = new TaxDTO();
        taxDTO.setId(tax.getId());
        taxDTO.setTaxNumber(tax.getTaxNumber());
        taxDTO.setPlanNumber(tax.getPlanNumber());
        taxDTO.setEntryText(tax.getEntryText());
        taxDTO.setBuilding(buildingMapper.BuildingToFeature(tax.getBuilding()));
        taxDTO.setPerson(tax.getPerson());
        taxDTO.setSource(tax.getSource());
        taxDTO.setInternalNotes(tax.getInternalNotes());
        taxDTO.setGeneralNotes(tax.getGeneralNotes());
        return taxDTO;
    }

    private Building getBuilding(Feature feature) {
        if (feature != null && feature.getProperties() instanceof BuildingProperties) {
            return buildingRepository.findById(feature.getId()).orElse(null);
        }
        return null;
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
}
