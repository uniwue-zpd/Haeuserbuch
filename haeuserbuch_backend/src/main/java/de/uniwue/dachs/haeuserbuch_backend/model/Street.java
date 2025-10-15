package de.uniwue.dachs.haeuserbuch_backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "STREET")
@Getter
@Setter
public class Street extends BaseEntity {
    private String name;

    @ElementCollection(targetClass = String.class)
    @CollectionTable(
            name = "street_alt_names",
            joinColumns = @JoinColumn(name = "street_id")
    )
    private List<String> altNames;

    private String description;
}
