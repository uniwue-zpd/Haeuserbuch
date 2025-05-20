package de.uniwue.dachs.haeuserbuch_backend.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BuildingDTO {
    private Long id;
    private String name;
    private String house_number;
    private String part_type;
    private String special_status;
    private String quarter;
    private String district;
    private String source;
    private String note;

    private List<List<List<Double>>> coordinates;
}
