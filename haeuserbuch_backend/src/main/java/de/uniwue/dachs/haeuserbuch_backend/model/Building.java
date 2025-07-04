package de.uniwue.dachs.haeuserbuch_backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Geometry;

import java.util.ArrayList;
import java.util.List;

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

    private String primary_source;

    @ElementCollection(targetClass = String.class)
    @CollectionTable(name = "building_secondary_sources", joinColumns = @JoinColumn(name = "building_id"))
    private List<String> secondary_sources = new ArrayList<>();

    private String notes;

    @Column(columnDefinition = "geometry(Geometry,25832)")
    private Geometry coordinates;
}

// TODO: Add fields for the cadastral number, possibly the relation to the cadastre
