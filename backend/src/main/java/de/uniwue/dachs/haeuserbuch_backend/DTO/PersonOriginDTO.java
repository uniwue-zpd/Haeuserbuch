package de.uniwue.dachs.haeuserbuch_backend.DTO;

import de.uniwue.dachs.haeuserbuch_backend.model.PlaceCertainty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PersonOriginDTO {
    private List<PlaceDTO> places = new ArrayList<>();
    private String originalText;
    private PlaceCertainty certainty;
}
