package de.uniwue.dachs.haeuserbuch_backend.service;

import de.uniwue.dachs.haeuserbuch_backend.DTO.BuildingDTO;
import de.uniwue.dachs.haeuserbuch_backend.DTO.TaxDTO;
import de.uniwue.dachs.haeuserbuch_backend.model.Building;
import de.uniwue.dachs.haeuserbuch_backend.model.Person;
import de.uniwue.dachs.haeuserbuch_backend.model.Source;
import de.uniwue.dachs.haeuserbuch_backend.model.Tax;
import de.uniwue.dachs.haeuserbuch_backend.repository.BuildingRepository;
import de.uniwue.dachs.haeuserbuch_backend.repository.PersonRepository;
import de.uniwue.dachs.haeuserbuch_backend.repository.SourceRepository;
import de.uniwue.dachs.haeuserbuch_backend.repository.TaxRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static de.uniwue.dachs.haeuserbuch_backend.utils.PostGIS.GeometryUtils.*;

@Service
public class TaxService {
    private final BuildingRepository buildingRepository;
    private final PersonRepository personRepository;
    private final SourceRepository sourceRepository;
    private final TaxRepository taxRepository;

    public TaxService(BuildingRepository buildingRepository,
                      PersonRepository personRepository,
                      SourceRepository sourceRepository,
                      TaxRepository taxRepository) {
        this.buildingRepository = buildingRepository;
        this.personRepository = personRepository;
        this.sourceRepository = sourceRepository;
        this.taxRepository = taxRepository;
    }

    // GET
    public List<TaxDTO> getAllTaxes() {
        List<Tax> taxes = taxRepository.findAll();
        List<TaxDTO> taxDTOs = new ArrayList<>();
        taxes.forEach(tax ->
                taxDTOs.add(taxToDto(tax))
        );
        return taxDTOs;
    }

    // GET
    public Optional<TaxDTO> getTaxById(Long id) {
        return taxRepository.findById(id)
                .map(this::taxToDto);
    }

    // POST
    @Transactional
    public void createTax(TaxDTO taxDTO) {
        Tax tax = DtoToTax(taxDTO);
        taxRepository.save(tax);
    }

    // DELETE
    @Transactional
    public void deleteTax(Long id) {
        if (!taxRepository.existsById(id)) {
            throw new IllegalArgumentException("Tax with id " + id + " does not exist");
        }
        taxRepository.deleteById(id);
    }

    // Helper methods
    private Tax DtoToTax(TaxDTO taxDTO) {
        Tax tax = new Tax();
        tax.setTax_number(taxDTO.getTax_number());
        tax.setPlan_number(taxDTO.getPlan_number());
        tax.setEntry_text(taxDTO.getEntry_text());
        tax.setBuilding(getOrSaveBuildingDTO(taxDTO.getBuilding()));
        tax.setPerson(getOrSavePerson(taxDTO.getPerson()));
        tax.setSource(getOrSaveSource(taxDTO.getSource()));
        tax.setNotes(taxDTO.getNotes());
        return tax;
    }

    private TaxDTO taxToDto(Tax tax) {
        TaxDTO taxDTO = new TaxDTO();
        taxDTO.setId(tax.getId());
        taxDTO.setTax_number(tax.getTax_number());
        taxDTO.setPlan_number(tax.getPlan_number());
        taxDTO.setEntry_text(tax.getEntry_text());
        taxDTO.setBuilding(BuildingToDTO(tax.getBuilding()));
        taxDTO.setPerson(tax.getPerson());
        taxDTO.setSource(tax.getSource());
        taxDTO.setNotes(tax.getNotes());
        return taxDTO;
    }

    private BuildingDTO BuildingToDTO(Building building) {
        BuildingDTO buildingDTO = new BuildingDTO();
        buildingDTO.setId(building.getId());
        buildingDTO.setName(building.getName());
        buildingDTO.setHouse_number(building.getHouse_number());
        buildingDTO.setPart_type(building.getPart_type());
        buildingDTO.setSpecial_status(building.getSpecial_status());
        buildingDTO.setQuarter(building.getQuarter());
        buildingDTO.setDistrict(building.getDistrict());
        buildingDTO.setSource(building.getSource());
        buildingDTO.setNote(building.getNote());
        buildingDTO.setCoordinates(convertPolygon(building.getCoordinates()));
        return buildingDTO;
    }

    private Building getOrSaveBuildingDTO(BuildingDTO buildingDTO) {
        if (buildingDTO == null) {
            return null;
        }
        if (buildingDTO.getId() != null) {
            return buildingRepository.findById(buildingDTO.getId()).orElse(null);
        }
        Building building = new Building();
        building.setName(buildingDTO.getName());
        building.setHouse_number(buildingDTO.getHouse_number());
        building.setPart_type(buildingDTO.getPart_type());
        building.setSpecial_status(buildingDTO.getSpecial_status());
        building.setQuarter(buildingDTO.getQuarter());
        building.setDistrict(buildingDTO.getDistrict());
        building.setSource(buildingDTO.getSource());
        building.setNote(buildingDTO.getNote());
        building.setCoordinates(createPolygon(buildingDTO.getCoordinates()));
        return buildingRepository.save(building);
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
