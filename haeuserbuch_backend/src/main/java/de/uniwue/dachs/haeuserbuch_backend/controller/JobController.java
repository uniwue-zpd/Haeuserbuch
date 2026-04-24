package de.uniwue.dachs.haeuserbuch_backend.controller;

import de.uniwue.dachs.haeuserbuch_backend.model.Job;
import de.uniwue.dachs.haeuserbuch_backend.service.JobService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jobs")
public class JobController {
    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping
    public ResponseEntity<List<Job>> getOccupations() {
        List<Job> jobs = jobService.getAllOccupations();
        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Job> getOccupationById(@PathVariable Long id) {
        return jobService.getOccupationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404).build());
    }

    @PostMapping
    public ResponseEntity<Job> createOccupation(@RequestBody Job job) {
        Job createdJob = jobService.createOccupation(job);
        return ResponseEntity.status(201).body(createdJob);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Job> updateOccupation(@PathVariable Long id, @RequestBody Job updatedJob) {
        try {
            Job job = jobService.updateOccupation(id, updatedJob);
            return ResponseEntity.ok(job);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOccupation(@PathVariable Long id) {
        try {
            jobService.deleteOccupation(id);
            return ResponseEntity.status(204).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).build();
        }
    }
}
