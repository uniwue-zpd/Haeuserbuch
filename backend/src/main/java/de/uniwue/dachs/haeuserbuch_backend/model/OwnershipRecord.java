package de.uniwue.dachs.haeuserbuch_backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

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
}
