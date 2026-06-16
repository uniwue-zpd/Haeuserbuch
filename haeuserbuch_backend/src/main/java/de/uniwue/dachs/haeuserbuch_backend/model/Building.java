package de.uniwue.dachs.haeuserbuch_backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Geometry;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "BUILDING")
@Getter
@Setter
public class Building extends BaseEntity {
    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JoinColumn(name = "building_id")
    private Set<BuildingName> names = new HashSet<>();

    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JoinColumn(name = "building_id")
    private Set<Address> addresses = new HashSet<>();

    private String partType;

    private String specialStatus;

    @ManyToOne
    @JoinColumn(name = "quarter_id")
    private Quarter quarter;

    @ManyToOne
    @JoinColumn(name = "district_id")
    private District district;

    private String houseNumber;

    private String districtHouseNumber;

    @ManyToMany
    @JoinTable(
            name = "building_primary_source",
            joinColumns = @JoinColumn(name = "building_id"),
            inverseJoinColumns = @JoinColumn(name = "primary_source_id")
    )
    private Set<Source> primarySources = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "building_secondary_source",
            joinColumns = @JoinColumn(name = "building_id"),
            inverseJoinColumns = @JoinColumn(name = "secondary_source_id")
    )
    private Set<Source> secondarySources = new HashSet<>();

    @Column(columnDefinition = "geometry(Geometry,4326)")
    private Geometry coordinates;
}
