package de.uniwue.dachs.haeuserbuch_backend.controller;

import de.uniwue.dachs.haeuserbuch_backend.DTO.PersonDTO;
import de.uniwue.dachs.haeuserbuch_backend.DTO.PreviewDTO.PersonPreviewDTO;
import de.uniwue.dachs.haeuserbuch_backend.model.PlaceCertainty;
import de.uniwue.dachs.haeuserbuch_backend.service.PersonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

@RestController
@RequestMapping("/persons")
public class PersonController {
    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping
    public ResponseEntity<List<PersonDTO>> getPersons() {
        List<PersonDTO> persons = personService.getAllPersons();
        return ResponseEntity.ok(persons);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonDTO> getPersonById(@PathVariable Long id) {
        return personService.getPersonById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404).build());
    }

    @GetMapping("/filter")
    public ResponseEntity<Set<PersonPreviewDTO>> searchPersons(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String sex,
            @RequestParam(required = false) String occupation,
            @RequestParam(required = false, value = "asscociated-building-id") Long associatedBuildingId,
            @RequestParam(required = false, value = "is-citizen") Boolean isCitizen,
            @RequestParam(required = false, value = "place-of-origin-id") Long placeOfOriginId,
            @RequestParam(required = false, value = "origin-certainty") PlaceCertainty originCertainty) {
        List<Object> paramsCount = Stream.<Object>of(
                name, sex, occupation, associatedBuildingId, isCitizen, placeOfOriginId, originCertainty
        )
                .filter(Objects::nonNull)
                .toList();
        if (paramsCount.isEmpty()) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(
                personService.filterPersons(
                        name, sex, occupation, associatedBuildingId, isCitizen, placeOfOriginId, originCertainty
                )
        );
    }

    @PostMapping
    public ResponseEntity<PersonDTO> createPerson(@RequestBody PersonDTO personDTO) {
        PersonDTO createdPerson = personService.createPerson(personDTO);
        return ResponseEntity.status(201).body(createdPerson);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PersonDTO> updatePerson(@PathVariable Long id, @RequestBody PersonDTO updatedPerson) {
        try {
            PersonDTO person = personService.updatePerson(id, updatedPerson);
            return ResponseEntity.status(200).body(person);
        } catch (Exception e) {
            return ResponseEntity.status(404).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePerson(@PathVariable Long id) {
        try {
            personService.deletePerson(id);
            return ResponseEntity.status(204).build();
        } catch (Exception e) {
            return ResponseEntity.status(404).build();
        }
    }
}
