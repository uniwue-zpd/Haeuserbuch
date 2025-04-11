package de.uniwue.dachs.haeuserbuch_backend.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BuildingDTO {
    private Long id;
    private String name;
    private String address;
    private String description;

    private Double[][] shape;
}
