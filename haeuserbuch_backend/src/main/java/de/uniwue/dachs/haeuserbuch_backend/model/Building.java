package de.uniwue.dachs.haeuserbuch_backend.model;

import de.uniwue.dachs.haeuserbuch_backend.embeddable.BuildingName;
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
    @ElementCollection
    @CollectionTable(
            name = "building_names",
            joinColumns = @JoinColumn(name = "building_id")
    )
    private Set<BuildingName> names = new HashSet<>();

    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JoinColumn(name = "building_id")
    private Set<Address> addresses = new HashSet<>();

    private String houseNumber;
    private String currentHouseNumber;

    @ManyToOne
    @JoinColumn(name = "street_id")
    private Street currentStreet;

    private String partType;

    private String specialStatus;

    @ManyToOne
    @JoinColumn(name = "quarter_id")
    private Quarter quarter;

    @ManyToOne
    @JoinColumn(name = "district_id")
    private District district;

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

    @Column(columnDefinition = "geometry(Geometry,25832)")
    private Geometry coordinates;
}
