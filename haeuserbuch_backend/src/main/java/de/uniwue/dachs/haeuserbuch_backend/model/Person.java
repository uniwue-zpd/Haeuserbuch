package de.uniwue.dachs.haeuserbuch_backend.model;

import de.uniwue.dachs.haeuserbuch_backend.embeddable.PersonOccupation;
import de.uniwue.dachs.haeuserbuch_backend.embeddable.PersonOrigin;
import de.uniwue.dachs.haeuserbuch_backend.embeddable.PersonReligion;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * Entity representing a person in the system, which may appear in various contexts such as citizen registries or
 * ownership records
 */
@Entity
@Table(name = "PERSON")
@Getter
@Setter
public class Person extends BaseEntity{

    private String firstName;

    private String lastName;

    private String fullName;

    @ElementCollection(targetClass = String.class)
    @CollectionTable(name = "person_alt_names", joinColumns = @JoinColumn(name = "person_id"))
    private Set<String> altNames = new HashSet<>();

    private String sex;

    @ManyToOne
    @JoinColumn(name = "building_id")
    private Building associatedBuilding;

    private Boolean isCitizen;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "originalText", column = @Column(name = "origin_original_text")),
            @AttributeOverride(name = "certainty", column = @Column(name = "origin_certainty"))
    })
    private PersonOrigin origin;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "originalText", column = @Column(name = "occupation_original_text"))
    })
    private PersonOccupation occupation;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "originalText", column = @Column(name = "religion_original_text"))
    })
    private PersonReligion religion;
  
    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JoinColumn(name = "person_id")
    private Set<Weaponry> weapons = new HashSet<>();
}
