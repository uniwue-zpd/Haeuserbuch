package de.uniwue.dachs.haeuserbuch_backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

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

    @ManyToOne(optional = false)
    @JoinColumn(name = "building_id", nullable = false)
    private Building building;

    @ManyToOne(optional = false)
    @JoinColumn(name = "source_id", nullable = false)
    private Source source;

    @Column(length = 2000)
    private String entry_text;

    private String notes;

    // TODO: Add fields for remaining ownership details
}
