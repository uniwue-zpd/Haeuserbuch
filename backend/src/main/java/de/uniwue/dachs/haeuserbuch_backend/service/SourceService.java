package de.uniwue.dachs.haeuserbuch_backend.service;

import de.uniwue.dachs.haeuserbuch_backend.DTO.SourceDTO;
import de.uniwue.dachs.haeuserbuch_backend.model.Source;
import de.uniwue.dachs.haeuserbuch_backend.repository.SourceRepository;
import de.uniwue.dachs.haeuserbuch_backend.search.SearchIndexAffecting;
import de.uniwue.dachs.haeuserbuch_backend.utils.Mappers.SourceMapper;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class SourceService {
    private final SourceRepository sourceRepository;
    private final SourceMapper sourceMapper;

    public SourceService(SourceRepository sourceRepository, SourceMapper sourceMapper) {
        this.sourceRepository = sourceRepository;
        this.sourceMapper = sourceMapper;
    }

    // GET all sources
    @Cacheable("sources")
    public List<Source> getAllSources() {

        return sourceRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    // GET source by ID
    @Cacheable(value = "sources", key = "#id")
    public Optional<Source> getSourceById(Long id) {
        return sourceRepository.findById(id);
    }

    // POST create a new source
    @Transactional
    @CacheEvict(value = "sources", allEntries = true)
    @SearchIndexAffecting
    public Source createSource(Source source) {
        return sourceRepository.save(source);
    }

    // PUT update an existing source
    @Transactional
    @CacheEvict(value = "sources", allEntries = true)
    @SearchIndexAffecting
    public Source updateSource(Long id, Source updatedSource) {
        return sourceRepository.findById(id)
                .map(existingSource -> {
                    existingSource.setType(updatedSource.getType());
                    existingSource.setTitle(updatedSource.getTitle());
                    existingSource.setSignature(updatedSource.getSignature());
                    existingSource.setDescription(updatedSource.getDescription());
                    existingSource.setLinks(updatedSource.getLinks());
                    existingSource.setAuthors(updatedSource.getAuthors());
                    existingSource.setInternalNotes(updatedSource.getInternalNotes());
                    existingSource.setGeneralNotes(updatedSource.getGeneralNotes());
                    return sourceRepository.save(existingSource);
                })
                .orElse(null);
    }

    // DELETE a source by ID
    @Transactional
    @CacheEvict(value = "sources", allEntries = true)
    @SearchIndexAffecting
    public void deleteSource(Long id) {
        if (!sourceRepository.existsById(id)) {
            throw new IllegalArgumentException("Source with ID " + id + " does not exist.");
        }
        sourceRepository.deleteById(id);
    }

    /**
     * Allows searching for sources using a query term
     * @param query Any search term as string
     * @return A {@link List} of {@link SourceDTO} matching the search term
     */
    public List<SourceDTO> searchSources(String query) {
        return sourceRepository.searchSources(query).stream()
                .map(sourceMapper::SourceToDTO)
                .filter(Objects::nonNull)
                .toList();
    }
}
