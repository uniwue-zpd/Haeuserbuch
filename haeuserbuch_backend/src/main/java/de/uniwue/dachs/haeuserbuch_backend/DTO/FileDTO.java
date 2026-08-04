package de.uniwue.dachs.haeuserbuch_backend.DTO;

import de.uniwue.dachs.haeuserbuch_backend.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileDTO extends BaseEntity {
    private String originalName;
    private String name;
    private String type;
    private Long size;
}
