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

    private Long year;

    private Long parcelNumber;

    private Long parcelNumberCounter;

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

    private String object;

    @ManyToOne
    @JoinColumn(name = "quarter_id")
    private Quarter quarter;

    @ManyToOne
    @JoinColumn(name = "district_id")
    private District district;

    private String propertyNumber;

    private String districtPropertyNumber;

    @ManyToMany
    @JoinTable(
            name = "building_source",
            joinColumns = @JoinColumn(name = "building_id"),
            inverseJoinColumns = @JoinColumn(name = "source_id")
    )
    private Set<Source> sources = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "building_literature",
            joinColumns = @JoinColumn(name = "building_id"),
            inverseJoinColumns = @JoinColumn(name = "source_id")
    )
    private Set<Source> literature = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "building_file",
            joinColumns = @JoinColumn(name = "building_id"),
            inverseJoinColumns = @JoinColumn(name = "file_id")
    )
    private Set<File> files = new HashSet<>();

    @Column(columnDefinition = "geometry(Geometry,4326)")
    private Geometry coordinates;
}
