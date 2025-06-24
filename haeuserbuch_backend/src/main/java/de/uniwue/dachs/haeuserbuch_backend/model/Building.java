package de.uniwue.dachs.haeuserbuch_backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Geometry;

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

    private String district_house_number;

    private String source;

    private String note;

    @Column(columnDefinition = "geometry(Geometry,25832)")
    private Geometry coordinates;
}

// TODO: Add fields for the cadastral number, possibly the relation to the cadastre
