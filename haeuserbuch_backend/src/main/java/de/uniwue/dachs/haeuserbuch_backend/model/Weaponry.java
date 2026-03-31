package de.uniwue.dachs.haeuserbuch_backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Weaponry extends BaseEntity {
    @ManyToOne
    private Weapon weapon;

    private String originalText;
}
