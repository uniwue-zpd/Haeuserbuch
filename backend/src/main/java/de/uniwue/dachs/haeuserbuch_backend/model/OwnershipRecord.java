package de.uniwue.dachs.haeuserbuch_backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "ownership_record")
@Getter
@Setter
public class OwnershipRecord extends BaseEntity {
    private String signature;

    // Stores the XML string representation of the ownership record.
    @Column(columnDefinition = "TEXT")
    private String entryXml;

    // Stores the plain text representation of the ownership record.
    @Column(columnDefinition = "TEXT")
    private String entryText;

    // Value of the <summe> element within <haus>.
    private Long sum;

    // Buildings referenced by the <distriktBesitzNummer> elements within <haus>.
    @ManyToMany
    @JoinTable(
            name = "ownership_record_building",
            joinColumns = @JoinColumn(name = "ownership_record_id"),
            inverseJoinColumns = @JoinColumn(name = "building_id")
    )
    private Set<Building> buildings = new HashSet<>();

    // Source referenced by the <quelle> element, e.g. the Viertelbuch of the entry.
    @ManyToOne
    @JoinColumn(name = "source_id")
    private Source source;
}
