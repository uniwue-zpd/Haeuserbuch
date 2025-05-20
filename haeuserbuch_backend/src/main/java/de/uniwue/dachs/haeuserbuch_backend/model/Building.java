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

    private String house_number;

    private String part_type;

    private String special_status;

    private String quarter;

    private String district;

    private String source;

    private String note;

    @Column(columnDefinition = "geometry(Polygon,25832)")
    private Polygon coordinates;
}

// TODO: Add fields for the cadastral number, possibly the relation to the cadastre
