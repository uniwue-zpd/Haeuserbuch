package de.uniwue.dachs.haeuserbuch_backend.service;

import de.uniwue.dachs.haeuserbuch_backend.DTO.OwnershipRecordDTO;
import de.uniwue.dachs.haeuserbuch_backend.model.Building;
import de.uniwue.dachs.haeuserbuch_backend.model.BuildingName;
import de.uniwue.dachs.haeuserbuch_backend.model.OwnershipRecord;
import de.uniwue.dachs.haeuserbuch_backend.model.Source;
import de.uniwue.dachs.haeuserbuch_backend.repository.BuildingRepository;
import de.uniwue.dachs.haeuserbuch_backend.repository.OwnershipRecordRepository;
import de.uniwue.dachs.haeuserbuch_backend.repository.SourceRepository;
import de.uniwue.dachs.haeuserbuch_backend.search.SearchIndexAffecting;
import de.uniwue.dachs.haeuserbuch_backend.utils.Mappers.OwnershipRecordMapper;
import de.uniwue.dachs.haeuserbuch_backend.utils.Mappers.OwnershipRecordMapper.BuildingNameInput;
import de.uniwue.dachs.haeuserbuch_backend.utils.XML.validators.OwnershipRecordValidator;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
public class OwnershipRecordService {

    private final OwnershipRecordRepository ownershipRecordRepository;
    private final BuildingRepository buildingRepository;
    private final SourceRepository sourceRepository;
    private final OwnershipRecordMapper ownershipRecordMapper;
    private final OwnershipRecordValidator ownershipRecordValidator;

    public OwnershipRecordService(
            OwnershipRecordRepository ownershipRecordRepository,
            BuildingRepository buildingRepository,
            SourceRepository sourceRepository,
            OwnershipRecordMapper ownershipRecordMapper,
            OwnershipRecordValidator ownershipRecordValidator
    ) {
        this.ownershipRecordRepository = ownershipRecordRepository;
        this.buildingRepository = buildingRepository;
        this.sourceRepository = sourceRepository;
        this.ownershipRecordMapper = ownershipRecordMapper;
        this.ownershipRecordValidator = ownershipRecordValidator;
    }

    /**
     * Creates an ownership record, links it to the referenced buildings and adds the building names
     * that are not stored yet. The XML must have passed {@link #validate(String)}.
     * @param xml the ownership record XML
     * @return the saved ownership record with its enriched XML
     */
    @Transactional
    @CacheEvict(value = "buildings", allEntries = true)
    @SearchIndexAffecting
    public OwnershipRecord create(String xml) {
        OwnershipRecord record = ownershipRecordMapper.toEntity(xml);
        addBuildingNames(record.getBuildings(), ownershipRecordMapper.extractBuildingNames(xml));
        record = ownershipRecordRepository.save(record);
        record.setEntryXml(ownershipRecordMapper.enrichXml(xml, record));
        return ownershipRecordRepository.save(record);
    }

    public Optional<String> getXmlById(Long id) {
        return ownershipRecordRepository.findById(id)
                .map(OwnershipRecord::getEntryXml);
    }

    public Optional<OwnershipRecordDTO> getById(Long id) {
        return ownershipRecordRepository.findById(id)
                .map(ownershipRecordMapper::toDTO);
    }

    /**
     * Validates the XML against the schema and checks that the referenced buildings and sources exist.
     * @param xml the ownership record XML
     * @return the validation errors, empty if the XML is valid
     */
    public List<String> validate(String xml) {
        List<String> errors = new ArrayList<>(ownershipRecordValidator.validate(xml));
        if (!errors.isEmpty()) return errors;

        // Not existing district property numbers
        for (String number : new LinkedHashSet<>(ownershipRecordMapper.extractDistrictPropertyNumbers(xml))) {
            boolean exists = buildingRepository.searchBuildings(number).stream()
                    .anyMatch(building -> number.equalsIgnoreCase(building.getDistrictPropertyNumber()));
            if (!exists) errors.add("No building found with district property number '" + number + "'");
        }

        // Not existing or ambiguous source title
        String sourceTitle = ownershipRecordMapper.extractSourceTitle(xml);
        if (sourceTitle != null) {
            int matches = ownershipRecordMapper.findSourcesByTitle(sourceTitle).size();
            if (matches == 0) errors.add("No source found with title '" + sourceTitle + "'");
            if (matches > 1) errors.add("Several sources found with title '" + sourceTitle + "'");
        }

        // Not existing source IDs
        ownershipRecordMapper.extractBuildingNames(xml).stream()
                .map(BuildingNameInput::sourceId)
                .filter(Objects::nonNull)
                .distinct()
                .filter(sourceId -> !sourceRepository.existsById(sourceId))
                .forEach(sourceId -> errors.add("No source found with ID " + sourceId));
        return errors;
    }

    /**
     * Adds the names to each building unless a name with the same text (trimmed, case-insensitive)
     * already exists. An existing name without a source gets the source of the new name.
     */
    private void addBuildingNames(Set<Building> buildings, List<BuildingNameInput> names) {
        for (BuildingNameInput input : names) {
            Source source = input.sourceId() == null ? null : sourceRepository.findById(input.sourceId()).orElseThrow();
            for (Building building : buildings) {
                Optional<BuildingName> existing = building.getNames().stream()
                        .filter(name -> name.getName() != null)
                        .filter(name -> name.getName().trim().equalsIgnoreCase(input.name()))
                        .findFirst();

                if (existing.isEmpty()) {
                    BuildingName name = new BuildingName();
                    name.setName(input.name());
                    name.setSource(source);
                    building.getNames().add(name);
                } else if (existing.get().getSource() == null && source != null) {
                    existing.get().setSource(source);
                }
            }
        }
    }
}
