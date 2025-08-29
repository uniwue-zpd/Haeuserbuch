package de.uniwue.dachs.haeuserbuch_backend.repository;

import de.uniwue.dachs.haeuserbuch_backend.model.Quarter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuarterRepository extends JpaRepository<Quarter, Long> {
}
