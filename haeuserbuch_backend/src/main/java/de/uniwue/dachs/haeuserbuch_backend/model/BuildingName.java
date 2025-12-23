package de.uniwue.dachs.haeuserbuch_backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class BuildingName extends BaseEntity {
    private String name;

    @ManyToOne
    private Source source;

    private String fromDate;

    private String toDate;
}
