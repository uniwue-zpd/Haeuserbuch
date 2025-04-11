package de.uniwue.dachs.haeuserbuch_backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Polygon;

@Entity
@Table(name = "BUILDING")
@Getter
@Setter
public class Building extends BaseEntity {

    private String name;

    private String address;

    private String description;

    @Column(columnDefinition = "geometry(Polygon,4326)")
    private Polygon shape;
}
