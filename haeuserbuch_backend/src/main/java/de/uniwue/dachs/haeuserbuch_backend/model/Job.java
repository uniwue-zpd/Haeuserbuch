package de.uniwue.dachs.haeuserbuch_backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "JOB")
@Getter
@Setter
public class Job extends BaseEntity {
    private String name;

    @ElementCollection(targetClass = String.class)
    @CollectionTable(name = "job_alt_names", joinColumns = @JoinColumn(name = "job_id"))
    private Set<String> altNames;

    private String description;
}
