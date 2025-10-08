package de.uniwue.dachs.haeuserbuch_backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Geometry;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "PLACE")
@Getter
@Setter
public class Place extends BaseEntity {
    private String realName;

    @ElementCollection(targetClass = String.class)
    @CollectionTable(name = "place_alt_names", joinColumns = @JoinColumn(name = "place_id"))
    private Set<String> altNames = new HashSet<>();

    private Boolean isUncertain;

    @Column(columnDefinition = "geometry(Geometry,4326)")
    private Geometry coordinates;
}
