package de.uniwue.dachs.haeuserbuch_backend.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "SOURCE")
@Getter
@Setter
public class Source extends BaseEntity {
    private String type;
    private String title;
    private String signature;
    private String description;

    @ElementCollection(targetClass = String.class)
    @CollectionTable(name = "source_links")
    private Set<String> links = new HashSet<>();

    @ElementCollection(targetClass = String.class)
    @CollectionTable(name = "source_authors")
    private Set<String> authors = new HashSet<>();
}
