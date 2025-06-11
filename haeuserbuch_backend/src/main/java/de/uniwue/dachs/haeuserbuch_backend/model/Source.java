package de.uniwue.dachs.haeuserbuch_backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "SOURCE")
@Getter
@Setter
public class Source extends BaseEntity {
    private String type;
    private String title;
    private String signature;
    private String description;
    private String notes;
}
