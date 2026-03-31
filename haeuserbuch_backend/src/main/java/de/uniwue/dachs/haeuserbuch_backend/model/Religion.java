package de.uniwue.dachs.haeuserbuch_backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "RELIGION")
@Getter
@Setter
public class Religion extends BaseEntity {
    private String name;
    private String description;
}
