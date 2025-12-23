package de.uniwue.dachs.haeuserbuch_backend.repository;

import de.uniwue.dachs.haeuserbuch_backend.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
