package de.uniwue.dachs.haeuserbuch_backend.repository;

import de.uniwue.dachs.haeuserbuch_backend.DTO.FullTextSearch.CitizenshipFullTextSearchResult;
import de.uniwue.dachs.haeuserbuch_backend.model.Citizenship;
import de.uniwue.dachs.haeuserbuch_backend.search.SearchMatching;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CitizenshipRepository extends JpaRepository<Citizenship, Long>, JpaSpecificationExecutor<Citizenship> {
    @Override
    @NonNull
    @EntityGraph(attributePaths = {
            "person",
            "primarySource",
            "secondarySource"
    })
    List<Citizenship> findAll();

    @Query(value = "SELECT cz.id, cz.signature, cz.ref_number, "
            + SearchMatching.CITIZENSHIP_HIGHLIGHT + " AS query_result FROM citizenship cz WHERE "
            + SearchMatching.CITIZENSHIP_MATCH,
            countQuery = "SELECT COUNT(*) FROM citizenship cz WHERE " + SearchMatching.CITIZENSHIP_MATCH,
            nativeQuery = true)
    Page<CitizenshipFullTextSearchResult> searchFullText(
            @Param("query") String query,
            @Param("fuzzy") boolean fuzzy,
            @Param("literalPattern") String literalPattern,
            @Param("highlightPattern") String highlightPattern,
            Pageable pageable
    );
}
