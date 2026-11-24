package de.uniwue.dachs.haeuserbuch_backend.controller;

import de.uniwue.dachs.haeuserbuch_backend.DTO.JobDTO;
import de.uniwue.dachs.haeuserbuch_backend.DTO.Register.RegisterDTO;
import de.uniwue.dachs.haeuserbuch_backend.model.Job;
import de.uniwue.dachs.haeuserbuch_backend.service.JobService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        List<Job> jobs = jobService.getAllJobs();
        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Job> getJobById(@PathVariable Long id) {
        return jobService.getJobById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404).build());
    }

    @PostMapping
    public ResponseEntity<Job> createJob(@RequestBody Job job) {
        Job createdJob = jobService.createJob(job);
        return ResponseEntity.status(201).body(createdJob);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Job> updateJob(@PathVariable Long id, @RequestBody Job updatedJob) {
        try {
            Job job = jobService.updateJob(id, updatedJob);
            return ResponseEntity.ok(job);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOccupation(@PathVariable Long id) {
        try {
            jobService.deleteJob(id);
            return ResponseEntity.status(204).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<JobDTO>> searchJobs(@RequestParam String query) {
        if (query == null || query.trim().length() < 3) {
            return ResponseEntity.ok(List.of());
        }
        List<JobDTO> jobDTOs = jobService.searchJobs(query);
        return ResponseEntity.ok(jobDTOs);
    }

    /**
     * Occupation register: paged occupations with their related entries, filtered by name or alternative name.
     * {@code type} is either "persons" (persons with the occupation) or "citizenships" (citizenships whose citizen,
     * guarantor or mentioned person has the occupation).
     */
    @GetMapping("/register")
    public ResponseEntity<Page<? extends RegisterDTO<?>>> getRegister(
            @RequestParam(name = "type") String type,
            @RequestParam(name = "name", required = false) String name,
            Pageable pageable
    ) {
        return switch (type) {
            case "persons" -> ResponseEntity.ok(jobService.getPersonRegister(name, pageable));
            case "citizenships" -> ResponseEntity.ok(jobService.getCitizenshipRegister(name, pageable));
            default -> ResponseEntity.badRequest().build();
        };
    }
}
