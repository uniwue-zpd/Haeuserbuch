package de.uniwue.dachs.haeuserbuch_backend.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class PlaceDTO {
    private Long id;
    private String realName;
    private Set<String> altNames;
}
