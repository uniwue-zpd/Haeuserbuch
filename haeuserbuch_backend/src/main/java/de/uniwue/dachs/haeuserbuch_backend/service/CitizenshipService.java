package de.uniwue.dachs.haeuserbuch_backend.service;

import de.uniwue.dachs.haeuserbuch_backend.DTO.CitizenshipDTO;
import de.uniwue.dachs.haeuserbuch_backend.model.*;
import de.uniwue.dachs.haeuserbuch_backend.repository.CitizenshipRepository;
import de.uniwue.dachs.haeuserbuch_backend.utils.Mappers.CitizenshipMapper;
import de.uniwue.dachs.haeuserbuch_backend.utils.Mappers.PersonMapper;
import de.uniwue.dachs.haeuserbuch_backend.utils.Mappers.SourceMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
    public CitizenshipDTO updateCitizenship(Long id, CitizenshipDTO updatedCitizenshipDTO) {
        return citizenshipRepository.findById(id)
                .map(existingCitizenship -> {
                    existingCitizenship.setSignature(updatedCitizenshipDTO.getSignature());
                    existingCitizenship.setPerson(personMapper.PreviewDTOToPerson(updatedCitizenshipDTO.getPerson()));
                    existingCitizenship.setMentionedPersons(personMapper.PreviewDTOsToPersons(updatedCitizenshipDTO.getMentionedPersons()));
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
    public void deleteCitizenship(Long id) {
        if (!citizenshipRepository.existsById(id)) {
            throw new RuntimeException("Citizenship with id '" + id + "' does not exist");
        }
        citizenshipRepository.deleteById(id);
    }
}
