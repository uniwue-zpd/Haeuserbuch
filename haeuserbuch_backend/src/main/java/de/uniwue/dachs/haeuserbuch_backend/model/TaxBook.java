package de.uniwue.dachs.haeuserbuch_backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "TAXBOOK")
@Getter
@Setter
public class TaxBook extends BaseEntity {
    private String title;
    private String signature;
}
