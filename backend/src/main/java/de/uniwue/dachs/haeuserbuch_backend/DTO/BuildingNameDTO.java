package de.uniwue.dachs.haeuserbuch_backend.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class BuildingNameDTO {
    /** null = new name; set = update the existing name of this building in place */
    private Long id;
    private String name;
    private String fromDate;
    private String toDate;
    private List<BuildingNameSourceDTO> sources = new ArrayList<>();
}
