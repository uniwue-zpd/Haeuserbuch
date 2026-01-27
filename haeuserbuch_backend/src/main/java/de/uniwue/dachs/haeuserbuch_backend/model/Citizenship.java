package de.uniwue.dachs.haeuserbuch_backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Entity representing a citizenship record associated with a person, including sources and relevant details
 */
@Entity
@Table(name = "CITIZENSHIP")
@Getter
@Setter
public class Citizenship extends BaseEntity {
    private String signature;

    private Long refNumber;

    @ManyToOne
    @JoinColumn(name = "person_id")
    private Person person;

    @ManyToOne(optional = false)
    @JoinColumn(name = "primary_source_id", nullable = false)
    private Source primarySource;

    @ManyToOne
    @JoinColumn(name = "secondary_source_id")
    private Source secondarySource;

    private String date;

    @Column(columnDefinition = "TEXT")
    private String entryText;

    private String addendum;
}
