package de.uniwue.dachs.haeuserbuch_backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "CITIZENSHIP")
@Getter
@Setter
public class Citizenship extends BaseEntity {
    private String signature;

    @ManyToOne
    @JoinColumn(name = "person_id")
    private Person person;

    @ManyToOne(optional = false)
    @JoinColumn(name = "source_id", nullable = false)
    private Source source;

    private Long number;

    private String date;

    @Column(columnDefinition = "TEXT")
    private String entryText;

    private String addendum;
}
