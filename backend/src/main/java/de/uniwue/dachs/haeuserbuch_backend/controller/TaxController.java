package de.uniwue.dachs.haeuserbuch_backend.controller;

import de.uniwue.dachs.haeuserbuch_backend.DTO.TaxDTO;
import de.uniwue.dachs.haeuserbuch_backend.model.Tax;
import de.uniwue.dachs.haeuserbuch_backend.service.TaxService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/taxes")
public class TaxController {
    private final TaxService taxService;

    public TaxController(TaxService taxService) {
        this.taxService = taxService;
    }

    @GetMapping
    public ResponseEntity<List<TaxDTO>> getAllTaxes() {
        List<TaxDTO> taxDTOS = taxService.getAllTaxes();
        return ResponseEntity.ok(taxDTOS);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaxDTO> getTaxById(@PathVariable Long id) {
        return taxService.getTaxById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404).build());
    }

    @PostMapping
    public ResponseEntity<Tax> createTax(@RequestBody TaxDTO taxDTO) {
        taxService.createTax(taxDTO);
        return ResponseEntity.status(201).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateTax(@PathVariable Long id, @RequestBody TaxDTO taxDTO) {
        try {
            taxService.updateTax(id, taxDTO);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).build();
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaxById(@PathVariable Long id) {
        try {
            taxService.deleteTax(id);
            return ResponseEntity.status(204).build();
        } catch (Exception e) {
            return ResponseEntity.status(404).build();
        }
    }
}
