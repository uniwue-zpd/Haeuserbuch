package de.uniwue.dachs.haeuserbuch_backend.service;

import de.uniwue.dachs.haeuserbuch_backend.DTO.CitizenshipDTO;
import de.uniwue.dachs.haeuserbuch_backend.DTO.FullTextSearch.CitizenshipFullTextSearchResult;
import de.uniwue.dachs.haeuserbuch_backend.DTO.PreviewDTO.CitizenshipPreviewDTO;
import de.uniwue.dachs.haeuserbuch_backend.model.*;
import de.uniwue.dachs.haeuserbuch_backend.repository.CitizenshipRepository;
import de.uniwue.dachs.haeuserbuch_backend.search.SearchIndexAffecting;
import de.uniwue.dachs.haeuserbuch_backend.search.SearchMatching;
import de.uniwue.dachs.haeuserbuch_backend.specification.CitizenshipSearchCriteria;
import de.uniwue.dachs.haeuserbuch_backend.specification.CitizenshipSpecification;
import de.uniwue.dachs.haeuserbuch_backend.utils.Mappers.CitizenshipMapper;
import de.uniwue.dachs.haeuserbuch_backend.utils.Mappers.PersonMapper;
import de.uniwue.dachs.haeuserbuch_backend.utils.Mappers.SourceMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class CitizenshipService {
    private final CitizenshipRepository citizenshipRepository;
    private final CitizenshipMapper citizenshipMapper;
    private final SourceMapper sourceMapper;
    private final PersonMapper personMapper;

    public CitizenshipService(CitizenshipRepository citizenshipRepository, CitizenshipMapper citizenshipMapper, SourceMapper sourceMapper, PersonMapper personMapper) {
        this.citizenshipRepository = citizenshipRepository;
        this.citizenshipMapper = citizenshipMapper;
        this.sourceMapper = sourceMapper;
        this.personMapper = personMapper;
    }

    /**
     * GET all citizenships
     * @return List of {@link CitizenshipDTO} objects
     */
    @Cacheable("citizenships")
    public List<CitizenshipDTO> getAllCitizenships() {
        return citizenshipRepository.findAll().stream()
                .map(citizenshipMapper::CitizenshipToDTO)
                .sorted(Comparator.comparing(CitizenshipDTO::getId))
                .toList();
    }

    /**
     * GET a {@link Page} of citizenships matching the given criteria
     * @param criteria {@link CitizenshipSearchCriteria}; {@code null} values and blank strings are ignored
     * @param pageable paging params
     * @return {@link Page} of {@link CitizenshipDTO} objects matching the criteria
     */
    public Page<CitizenshipDTO> getPagedCitizenships(CitizenshipSearchCriteria criteria, Pageable pageable) {
        return citizenshipRepository.findAll(buildSpecification(criteria), pageable)
                .map(citizenshipMapper::CitizenshipToDTO);
    }

    /**
     * GET an unpaged list of citizenship previews matching the given criteria
     * @param criteria {@link CitizenshipSearchCriteria}; {@code null} values and blank strings are ignored
     * @return {@link List} of {@link CitizenshipPreviewDTO} objects sorted by id
     */
    public List<CitizenshipPreviewDTO> filterCitizenships(CitizenshipSearchCriteria criteria) {
        return citizenshipRepository.findAll(buildSpecification(criteria)).stream()
                .map(citizenshipMapper::CitizenshipToPreviewDTO)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(CitizenshipPreviewDTO::getId))
                .toList();
    }

    /**
     * Builds a {@link Specification} that combines all set criteria with AND.
     * @param c search criteria; {@code null} values and blank strings are ignored
     */
    private Specification<Citizenship> buildSpecification(CitizenshipSearchCriteria c) {
        Specification<Citizenship> spec = Specification.where(null);
        if (c == null) return spec;
        if (hasText(c.refNumber())) spec = spec.and(CitizenshipSpecification.hasRefNumber(c.refNumber()));
        if (hasText(c.signature())) spec = spec.and(CitizenshipSpecification.hasSignature(c.signature()));
        if (hasText(c.naturalizedPerson())) spec = spec.and(CitizenshipSpecification.hasNaturalizedPerson(c.naturalizedPerson()));
        if (c.naturalizedPersonId() != null) spec = spec.and(CitizenshipSpecification.hasNaturalizedPersonId(c.naturalizedPersonId()));
        if (hasText(c.naturalizedPersonJob())) spec = spec.and(CitizenshipSpecification.hasNaturalizedPersonJob(c.naturalizedPersonJob()));
        if (hasText(c.naturalizedPersonOrigin())) spec = spec.and(CitizenshipSpecification.hasNaturalizedPersonOrigin(c.naturalizedPersonOrigin()));
        if (hasText(c.guarantor())) spec = spec.and(CitizenshipSpecification.hasGuarantor(c.guarantor()));
        if (c.guarantorId() != null) spec = spec.and(CitizenshipSpecification.hasGuarantorId(c.guarantorId()));
        if (hasText(c.guarantorJob())) spec = spec.and(CitizenshipSpecification.hasGuarantorJob(c.guarantorJob()));
        if (hasText(c.guarantorOrigin())) spec = spec.and(CitizenshipSpecification.hasGuarantorOrigin(c.guarantorOrigin()));
        if (c.yearNaturalization() != null) spec = spec.and(CitizenshipSpecification.hasYearNaturalization(c.yearNaturalization()));
        if (c.yearFrom() != null) spec = spec.and(CitizenshipSpecification.hasYearFrom(c.yearFrom()));
        if (c.yearTo() != null) spec = spec.and(CitizenshipSpecification.hasYearTo(c.yearTo()));
        if (hasText(c.primarySource())) spec = spec.and(CitizenshipSpecification.hasPrimarySource(c.primarySource()));
        if (hasText(c.secondarySource())) spec = spec.and(CitizenshipSpecification.hasSecondarySource(c.secondarySource()));
        return spec;
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    /**
     * GET citizenship by id
     * @param id of the citizenship
     * @return Optional of {@link CitizenshipDTO}
     */
    @Cacheable(value = "citizenships", key = "#id")
    public Optional<CitizenshipDTO> getCitizenshipById(Long id) {
        return citizenshipRepository.findById(id)
                .map(citizenshipMapper::CitizenshipToDTO);
    }

    /**
     * POST create new citizenship
     * @param citizenshipDTO to create
     */
    @Transactional
    @CacheEvict(value = "citizenships", allEntries = true)
    @SearchIndexAffecting
    public CitizenshipDTO createCitizenship(CitizenshipDTO citizenshipDTO) {
        return citizenshipMapper.CitizenshipToDTO(
                citizenshipRepository.save(citizenshipMapper.DTOToCitizenship(citizenshipDTO))
        );
    }

    /**
     * PUT update existing citizenship
     * @param id of the citizenship to update
     * @param updatedCitizenshipDTO with updated data
     */
    @Transactional
    @CacheEvict(value = "citizenships", allEntries = true)
    @SearchIndexAffecting
    public CitizenshipDTO updateCitizenship(Long id, CitizenshipDTO updatedCitizenshipDTO) {
        return citizenshipRepository.findById(id)
                .map(existingCitizenship -> {
                    existingCitizenship.setSignature(updatedCitizenshipDTO.getSignature());
                    existingCitizenship.setPerson(personMapper.PreviewDTOToPerson(updatedCitizenshipDTO.getPerson()));
                    existingCitizenship.setMentionedPersons(personMapper.PreviewDTOsToPersons(updatedCitizenshipDTO.getMentionedPersons()));
                    existingCitizenship.setGuarantors(personMapper.PreviewDTOsToPersons(updatedCitizenshipDTO.getGuarantors()));
                    existingCitizenship.setPrimarySource(sourceMapper.SourceDTOToSource(updatedCitizenshipDTO.getPrimarySource()));
                    existingCitizenship.setSecondarySource(sourceMapper.SourceDTOToSource(updatedCitizenshipDTO.getSecondarySource()));
                    existingCitizenship.setRefNumber(updatedCitizenshipDTO.getRefNumber());
                    existingCitizenship.setDateNaturalization(updatedCitizenshipDTO.getDateNaturalization());
                    existingCitizenship.setDateMisc(updatedCitizenshipDTO.getDateMisc());
                    existingCitizenship.setEntryText(updatedCitizenshipDTO.getEntryText());
                    existingCitizenship.setAddendum(updatedCitizenshipDTO.getAddendum());
                    existingCitizenship.setInternalNotes(updatedCitizenshipDTO.getInternalNotes());
                    existingCitizenship.setGeneralNotes(updatedCitizenshipDTO.getGeneralNotes());
                    Citizenship saved = citizenshipRepository.save(existingCitizenship);
                    return citizenshipMapper.CitizenshipToDTO(saved);
                })
                .orElseThrow(() -> new EntityNotFoundException("Citizenship with id '" + id + "' does not exist"));
    }

    /**
     * DELETE citizenship by id
     * @param id of the citizenship to delete
     */
    @Transactional
    @CacheEvict(value = "citizenships", allEntries = true)
    @SearchIndexAffecting
    public void deleteCitizenship(Long id) {
        if (!citizenshipRepository.existsById(id)) {
            throw new RuntimeException("Citizenship with id '" + id + "' does not exist");
        }
        citizenshipRepository.deleteById(id);
    }

    /** Searches register text using the same linguistic/literal modes as global search. */
    public Page<CitizenshipFullTextSearchResult> searchCitizenshipFullText(String query, boolean fuzzy, Pageable pageable) {
        if (query == null || query.isBlank()) return Page.empty(pageable);
        String normalizedQuery = query.trim();
        return citizenshipRepository.searchFullText(normalizedQuery, fuzzy,
                SearchMatching.literalPattern(normalizedQuery), SearchMatching.highlightPattern(normalizedQuery), pageable);
    }
}
