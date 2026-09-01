package de.uniwue.dachs.haeuserbuch_backend.repository;

import de.uniwue.dachs.haeuserbuch_backend.model.OwnershipRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OwnershipRecordRepository extends JpaRepository<OwnershipRecord, Long> {
}
