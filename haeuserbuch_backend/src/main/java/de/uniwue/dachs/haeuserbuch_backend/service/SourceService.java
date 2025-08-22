package de.uniwue.dachs.haeuserbuch_backend.service;

import de.uniwue.dachs.haeuserbuch_backend.model.Source;
import de.uniwue.dachs.haeuserbuch_backend.repository.SourceRepository;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SourceService {
    private final SourceRepository sourceRepository;

    public SourceService(SourceRepository sourceRepository) {
        this.sourceRepository = sourceRepository;
    }

    // GET all sources
    @Cacheable("sources")
    public List<Source> getAllSources() {
        return sourceRepository.findAll();
    }

    // GET source by ID
    @Cacheable(value = "sources", key = "#id")
    public Optional<Source> getSourceById(Long id) {
        return sourceRepository.findById(id);
    }

    // POST create a new source
    @Transactional
    @CacheEvict(value = "sources", allEntries = true)
    public Source createSource(Source source) {
        return sourceRepository.save(source);
    }

    // PUT update an existing source
    @Transactional
    @CachePut(value = "sources", key = "#id")
    public Source updateSource(Long id, Source updatedSource) {
        return sourceRepository.findById(id)
                .map(existingSource -> {
                    existingSource.setType(updatedSource.getType());
                    existingSource.setTitle(updatedSource.getTitle());
                    existingSource.setSignature(updatedSource.getSignature());
                    existingSource.setDescription(updatedSource.getDescription());
                    existingSource.setInternalNotes(updatedSource.getInternalNotes());
                    existingSource.setGeneralNotes(updatedSource.getGeneralNotes());
                    return sourceRepository.save(existingSource);
                })
                .orElse(null);
    }

    // DELETE a source by ID
    @Transactional
    @CacheEvict(value = "sources", allEntries = true)
    public void deleteSource(Long id) {
        if (!sourceRepository.existsById(id)) {
            throw new IllegalArgumentException("Source with ID " + id + " does not exist.");
        }
        sourceRepository.deleteById(id);
    }
}
