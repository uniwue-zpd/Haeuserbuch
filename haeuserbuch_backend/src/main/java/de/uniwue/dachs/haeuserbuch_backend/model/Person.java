package de.uniwue.dachs.haeuserbuch_backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "PERSON")
@Getter
@Setter
public class Person extends BaseEntity{

    private String firstName;

    private String lastName;

    private String fullName;

    private String sex;

    private String occupation;

    private String occupationCategory;

    private Boolean isCitizen;

    private String confession;
}
