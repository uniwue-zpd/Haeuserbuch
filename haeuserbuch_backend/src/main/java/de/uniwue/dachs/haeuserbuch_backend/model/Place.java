package de.uniwue.dachs.haeuserbuch_backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Point;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "PLACE")
@Getter
@Setter
public class Place extends BaseEntity {
    private String real_name;

    @ElementCollection(targetClass = String.class)
    @CollectionTable(name = "place_alt_names", joinColumns = @JoinColumn(name = "place_id"))
    private List<String> alt_names = new ArrayList<>();

    @Column(columnDefinition = "geometry(Point,4326)")
    private Point location;

    // TODO: Use DTO (latitude and longitude as separate fields) in order to create a point for each place
}
