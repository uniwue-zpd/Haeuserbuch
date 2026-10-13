package de.uniwue.dachs.haeuserbuch_backend.DTO;

import de.uniwue.dachs.haeuserbuch_backend.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class OwnershipRecordDTO extends BaseEntity {
    private String signature;
    private String entryText;
    private Long sum;
    private Set<BuildingDTO> buildings;
}
