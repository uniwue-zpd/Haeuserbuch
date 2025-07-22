package de.uniwue.dachs.haeuserbuch_backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "CITIZENSHIP")
@Getter
@Setter
public class Citizenship extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    @ManyToOne(optional = false)
    @JoinColumn(name = "source_id", nullable = false)
    private Source source;

    @ManyToOne
    @JoinColumn(name = "place_id")
    private Place place;

    private Long number;

    private String date;

    @Column(columnDefinition = "TEXT")
    private String entryText;

    private String addendum;

    private String notes;
}
