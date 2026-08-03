package de.uniwue.dachs.haeuserbuch_backend.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class File extends BaseEntity {
    private String fileName;
    private String filePath;
    private String type;
    private String size;
}
