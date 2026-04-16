package de.uniwue.dachs.haeuserbuch_backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * Entity representing a citizenship record associated with a person, including sources and relevant details
 */
@Entity
@Table(name = "CITIZENSHIP")
@Getter
@Setter
public class Citizenship extends BaseEntity {
    private String signature;

    private String refNumber;

    @ManyToOne
    @JoinColumn(name = "person_id")
    private Person person;

    @ManyToMany
    @JoinTable(
            name = "citizenship_mentioned_person",
            joinColumns = @JoinColumn(name = "citizenship_id"),
            inverseJoinColumns = @JoinColumn(name = "person_id")
    )
    private Set<Person> mentionedPersons = new HashSet<>();

    @ManyToOne(optional = false)
    @JoinColumn(name = "primary_source_id", nullable = false)
    private Source primarySource;

    @ManyToOne
    @JoinColumn(name = "secondary_source_id")
    private Source secondarySource;

    private String dateNaturalization;

    private String dateMisc;

    @Column(columnDefinition = "TEXT")
    private String entryText;

    private String addendum;
}
