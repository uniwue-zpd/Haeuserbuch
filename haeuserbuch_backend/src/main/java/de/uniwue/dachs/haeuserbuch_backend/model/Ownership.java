package de.uniwue.dachs.haeuserbuch_backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "OWNERSHIP")
@Getter
@Setter
public class Ownership extends BaseEntity {
    private String type;

    private String date;

    private Long price;

    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private Person owner;

    @ManyToOne(optional = false)
    @JoinColumn(name = "seller_id")
    private Person seller;

    @ManyToMany
    @JoinTable(
            name = "ownership_building",
            joinColumns = @JoinColumn(name = "ownership_id"),
            inverseJoinColumns = @JoinColumn(name = "building_id")
    )
    private Set<Building> buildings = new HashSet<>();

    @ManyToOne(optional = false)
    @JoinColumn(name = "source_id", nullable = false)
    private Source source;

    @Column(columnDefinition = "TEXT")
    private String entryText;
}
