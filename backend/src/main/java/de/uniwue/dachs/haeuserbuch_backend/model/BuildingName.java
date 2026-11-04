package de.uniwue.dachs.haeuserbuch_backend.model;

import de.uniwue.dachs.haeuserbuch_backend.embeddable.BuildingNameSource;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class BuildingName extends BaseEntity {
    private String name;

    private String fromDate;

    private String toDate;

    @ElementCollection
    @CollectionTable(name = "building_name_source", joinColumns = @JoinColumn(name = "building_name_id"))
    @OrderColumn(name = "position")
    private List<BuildingNameSource> sources = new ArrayList<>();
}
