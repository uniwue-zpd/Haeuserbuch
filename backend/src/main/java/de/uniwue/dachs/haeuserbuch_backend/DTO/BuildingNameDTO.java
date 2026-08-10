package de.uniwue.dachs.haeuserbuch_backend.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BuildingNameDTO {
    private Long id;
    private String name;
    private SourceDTO source;
    private String fromDate;
    private String toDate;
}
