package de.uniwue.dachs.haeuserbuch_backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "OCCUPATION")
@Getter
@Setter
public class Occupation extends BaseEntity {
    private String name;
}
