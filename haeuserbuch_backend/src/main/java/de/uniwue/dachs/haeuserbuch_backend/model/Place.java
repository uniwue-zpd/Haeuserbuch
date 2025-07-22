package de.uniwue.dachs.haeuserbuch_backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Geometry;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "PLACE")
@Getter
@Setter
public class Place extends BaseEntity {
    private String realName;

    @ElementCollection(targetClass = String.class)
    @CollectionTable(name = "place_alt_names", joinColumns = @JoinColumn(name = "place_id"))
    private List<String> altNames = new ArrayList<>();

    @Column(columnDefinition = "geometry(Geometry,25832)")
    private Geometry coordinates;

    private String notes;

    // TODO: Use DTO (latitude and longitude as separate fields) in order to create a point for each place
}
