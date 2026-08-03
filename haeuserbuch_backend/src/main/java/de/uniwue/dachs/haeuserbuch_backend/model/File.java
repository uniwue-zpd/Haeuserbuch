package de.uniwue.dachs.haeuserbuch_backend.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class File extends BaseEntity {
    private String originalName;
    private String name;
    private String path;
    private String type;
    private Long size;
}
