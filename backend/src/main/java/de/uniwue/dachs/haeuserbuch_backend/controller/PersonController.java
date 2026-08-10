package de.uniwue.dachs.haeuserbuch_backend.controller;

import de.uniwue.dachs.haeuserbuch_backend.DTO.PersonDTO;
import de.uniwue.dachs.haeuserbuch_backend.DTO.PreviewDTO.PersonPreviewDTO;
import de.uniwue.dachs.haeuserbuch_backend.model.PlaceCertainty;
import de.uniwue.dachs.haeuserbuch_backend.service.PersonService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

    @GetMapping("/all")
    public ResponseEntity<List<PersonDTO>> getPersons() {
        List<PersonDTO> persons = personService.getAllPersons();
        return ResponseEntity.ok(persons);
    }

    @GetMapping
    public ResponseEntity<Page<PersonDTO>> getPagedPeople(
            @PageableDefault(sort = "fullName", direction = Sort.Direction.ASC)
            Pageable pageable,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String sex,
            @RequestParam(required = false) String job,
            @RequestParam(required = false, value = "job-id") Long jobId,
            @RequestParam(required = false, value = "associated-building") String associatedBuilding,
            @RequestParam(required = false, value = "associated-building-id") Long associatedBuildingId,
            @RequestParam(required = false, value = "is-citizen") Boolean isCitizen,
            @RequestParam(required = false, value = "place-of-origin") String placeOfOrigin,
            @RequestParam(required = false, value = "place-of-origin-id") Long placeOfOriginId,
            @RequestParam(required = false, value = "origin-certainty") PlaceCertainty originCertainty,
            @RequestParam(required = false) String religion,
            @RequestParam(required = false, value = "religion-id") Long religionId,
            @RequestParam(required = false) String weapon,
            @RequestParam(required = false, value = "weapon-id") Long weaponId
    ) {
        Page<PersonDTO> people = personService.getPagedPeople(
                pageable, name, sex, job, jobId, associatedBuilding, associatedBuildingId, isCitizen, placeOfOrigin, placeOfOriginId, originCertainty, religion, religionId, weapon, weaponId
        );
        return ResponseEntity.ok(people);
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
            @RequestParam(required = false) String job,
            @RequestParam(required = false, value = "job-id") Long jobId,
            @RequestParam(required = false, value = "associated-building") String associatedBuilding,
            @RequestParam(required = false, value = "associated-building-id") Long associatedBuildingId,
            @RequestParam(required = false, value = "is-citizen") Boolean isCitizen,
            @RequestParam(required = false, value = "place-of-origin") String placeOfOrigin,
            @RequestParam(required = false, value = "place-of-origin-id") Long placeOfOriginId,
            @RequestParam(required = false, value = "origin-certainty") PlaceCertainty originCertainty,
            @RequestParam(required = false) String religion,
            @RequestParam(required = false, value = "religion-id") Long religionId,
            @RequestParam(required = false) String weapon,
            @RequestParam(required = false, value = "weapon-id") Long weaponId
    ) {
        List<Object> paramsCount = Stream.<Object>of(
                name, sex, job, jobId, associatedBuilding, associatedBuildingId, isCitizen, placeOfOrigin, placeOfOriginId, originCertainty, religion, religionId, weapon, weaponId
                ).filter(Objects::nonNull).toList();
        if (paramsCount.isEmpty()) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(personService.filterPersons(
                name, sex, job, jobId, associatedBuilding, associatedBuildingId, isCitizen, placeOfOrigin, placeOfOriginId, originCertainty, religion, religionId, weapon, weaponId
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

    @GetMapping("/search")
    public ResponseEntity<List<PersonPreviewDTO>> searchPeople(@RequestParam String query) {
        if (query == null || query.trim().length() < 3) {
            return ResponseEntity.ok(List.of());
        }
        List<PersonPreviewDTO> personPreviewDTOS = personService.searchPeople(query);
        return ResponseEntity.ok(personPreviewDTOS);
    }
}
