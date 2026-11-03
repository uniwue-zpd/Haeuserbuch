package de.uniwue.dachs.haeuserbuch_backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.Generated;
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

    @ManyToMany
    @JoinTable(
            name = "citizenship_guarantor",
            joinColumns = @JoinColumn(name = "citizenship_id"),
            inverseJoinColumns = @JoinColumn(name = "person_id")
    )
    private Set<Person> guarantors = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "primary_source_id")
    private Source primarySource;

    @ManyToOne
    @JoinColumn(name = "secondary_source_id")
    private Source secondarySource;

    private String dateNaturalization;

    /** Year derived by the database from {@link #dateNaturalization} (generated column, read-only) */
    @Column(name = "year_naturalization", insertable = false, updatable = false)
    @Generated
    private Integer yearNaturalization;

    private String dateMisc;

    @Column(columnDefinition = "TEXT")
    private String entryText;

    private String addendum;
}
