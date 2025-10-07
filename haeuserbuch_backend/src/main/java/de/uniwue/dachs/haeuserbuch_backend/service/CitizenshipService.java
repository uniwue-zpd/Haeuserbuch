package de.uniwue.dachs.haeuserbuch_backend.service;

import de.uniwue.dachs.haeuserbuch_backend.DTO.CitizenshipDTO;
import de.uniwue.dachs.haeuserbuch_backend.model.*;
import de.uniwue.dachs.haeuserbuch_backend.repository.CitizenshipRepository;
import de.uniwue.dachs.haeuserbuch_backend.utils.Mappers.CitizenshipMapper;
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

    public CitizenshipService(CitizenshipRepository citizenshipRepository, CitizenshipMapper citizenshipMapper) {
        this.citizenshipRepository = citizenshipRepository;
        this.citizenshipMapper = citizenshipMapper;
    }

    // GET all citizenships
    @Cacheable("citizenships")
    public List<CitizenshipDTO> getAllCitizenships() {
        return citizenshipRepository.findAll().stream()
                .map(citizenshipMapper::CitizenshipToDTO)
                .sorted(Comparator.comparing(CitizenshipDTO::getId))
                .toList();
    }

    // GET citizenship by ID
    @Cacheable(value = "citizenships", key = "#id")
    public Optional<CitizenshipDTO> getCitizenshipById(Long id) {
        return citizenshipRepository.findById(id)
                .map(citizenshipMapper::CitizenshipToDTO);
    }

    // POST
    @Transactional
    @CacheEvict(value = "citizenships", allEntries = true)
    public void createCitizenship(CitizenshipDTO citizenshipDTO) {
        Citizenship citizenship = citizenshipMapper.CitizenshipDTOToCitizenship(citizenshipDTO);
        citizenshipRepository.save(citizenship);
    }

    // PUT
    @Transactional
    @CacheEvict(value = "citizenships", key = "#id")
    public void updateCitizenship(Long id, CitizenshipDTO updatedCitizenshipDTO) {
        citizenshipRepository.findById(id)
                .map(existingCitizenship -> {
                    existingCitizenship.setPersons(citizenshipMapper.getOrSavePersons(updatedCitizenshipDTO.getPersons()));
                    existingCitizenship.setSource(citizenshipMapper.getSource(updatedCitizenshipDTO.getSource()));
                    existingCitizenship.setPlace(citizenshipMapper.getPlace(updatedCitizenshipDTO.getPlace()));
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
}
