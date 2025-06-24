package de.uniwue.dachs.haeuserbuch_backend.DTO.GeoJsonDTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = BuildingProperties.class, name = "building"),
        @JsonSubTypes.Type(value = PlaceProperties.class, name = "place"),
})
@Getter
@Setter
public abstract class Properties {
    @JsonIgnore
    private String type;
}
