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

    private String first_name;

    private String last_name;

    private String topographic_surname;

    private String full_name;

    private String occupation;

    private Boolean is_citizen;

    private String confession;

    // TODO: Add column for places (Set/List)
}
