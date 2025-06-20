package de.uniwue.dachs.haeuserbuch_backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "OWNERSHIP")
@Getter
@Setter
public class Ownership extends BaseEntity {
    @ManyToMany
    @JoinTable(
            name = "OWNERSHIP_PERSON",
            joinColumns = @JoinColumn(name = "ownership_id"),
            inverseJoinColumns = @JoinColumn(name = "person_id")
    )
    private List<Person> persons;

    @ManyToMany
    @JoinTable(
            name = "OWNERSHIP_BUILDING",
            joinColumns = @JoinColumn(name = "ownership_id"),
            inverseJoinColumns = @JoinColumn(name = "building_id")
    )
    private List<Building> buildings;

    @ManyToOne(optional = false)
    @JoinColumn(name = "source_id", nullable = false)
    private Source source;

    private String entry_text;

    private String notes;

    // TODO: Add fields for remaining ownership details
}
