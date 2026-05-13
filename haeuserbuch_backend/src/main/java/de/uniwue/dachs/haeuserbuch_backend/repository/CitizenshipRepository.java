package de.uniwue.dachs.haeuserbuch_backend.repository;

import de.uniwue.dachs.haeuserbuch_backend.model.Citizenship;
import lombok.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
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
}
