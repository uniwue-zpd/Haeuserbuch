package de.uniwue.dachs.haeuserbuch_backend.controller;

import de.uniwue.dachs.haeuserbuch_backend.service.OwnershipService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ownerships")
public class OwnershipController {
    private final OwnershipService ownershipService;

    public OwnershipController(OwnershipService ownershipService) {
        this.ownershipService = ownershipService;
    }

    // TODO: Implement all endpoints
}
