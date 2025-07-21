package de.uniwue.dachs.haeuserbuch_backend.service;

import de.uniwue.dachs.haeuserbuch_backend.repository.OwnershipRepository;
import org.springframework.stereotype.Service;

@Service
public class OwnershipService {
    private final OwnershipRepository ownershipRepository;

    public OwnershipService(OwnershipRepository ownershipRepository) {
        this.ownershipRepository = ownershipRepository;
    }

    // TODO: Implement all methods based on the OwnershipDTO
}
