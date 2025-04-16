package de.uniwue.dachs.haeuserbuch_backend.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PlaceDTO {
    private Long id;
    private String real_name;
    private List<String> alt_names = new ArrayList<>();
    private List<Double> coordinates = new ArrayList<>();
}
