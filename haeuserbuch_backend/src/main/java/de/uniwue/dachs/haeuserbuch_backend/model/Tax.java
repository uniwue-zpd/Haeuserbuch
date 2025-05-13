package de.uniwue.dachs.haeuserbuch_backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "TAX")
@Getter
@Setter
public class Tax extends BaseEntity {
    private String tax_number;

    private String plan_number;

    private String entry_text;

    @ManyToOne(optional = false)
    @JoinColumn(name = "building_id")
    private Building building;

    @ManyToOne
    @JoinColumn(name = "person_id")
    private Person person;

    @ManyToOne(optional = false)
    @JoinColumn(name = "taxbook_id")
    private TaxBook taxbook;
}
