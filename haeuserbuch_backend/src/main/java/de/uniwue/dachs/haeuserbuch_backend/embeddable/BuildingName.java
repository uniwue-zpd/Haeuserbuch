package de.uniwue.dachs.haeuserbuch_backend.embeddable;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class BuildingName {
    private String name;
    private String source;
}
