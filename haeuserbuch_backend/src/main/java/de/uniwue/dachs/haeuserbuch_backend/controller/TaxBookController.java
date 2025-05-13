package de.uniwue.dachs.haeuserbuch_backend.controller;

import de.uniwue.dachs.haeuserbuch_backend.model.TaxBook;
import de.uniwue.dachs.haeuserbuch_backend.repository.TaxBookRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/taxbooks")
public class TaxBookController {
    private final TaxBookRepository taxBookRepository;

    public TaxBookController(TaxBookRepository taxBookRepository) {
        this.taxBookRepository = taxBookRepository;
    }

    @GetMapping
    public ResponseEntity<List<TaxBook>> getTaxBooks() {
        List<TaxBook> taxBooks = taxBookRepository.findAll();
        return ResponseEntity.ok().body(taxBooks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaxBook> getTaxBookById(@PathVariable Long id) {
        return taxBookRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404).build());
    }

    @PostMapping
    public ResponseEntity<TaxBook> createTaxBook(@RequestBody TaxBook taxBook) {
        TaxBook savedTaxBook = taxBookRepository.save(taxBook);
        return ResponseEntity.status(201).body(savedTaxBook);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaxBook(@PathVariable Long id) {
        try {
            taxBookRepository.deleteById(id);
            return ResponseEntity.status(204).build();
        } catch (Exception e) {
            return ResponseEntity.status(404).build();
        }
    }
}
