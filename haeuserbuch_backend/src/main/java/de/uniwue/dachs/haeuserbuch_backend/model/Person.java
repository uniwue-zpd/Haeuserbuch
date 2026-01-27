package de.uniwue.dachs.haeuserbuch_backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

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

    private String occupation;

    private String occupationCategory;

    private Boolean isCitizen;

    private String confession;

    @ManyToOne
    @JoinColumn(name = "place_id")
    private Place origin;
}
