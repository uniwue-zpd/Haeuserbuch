package de.uniwue.dachs.haeuserbuch_backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "DISTRICT")
@Getter
@Setter
public class District extends BaseEntity {
    private String name;
}
