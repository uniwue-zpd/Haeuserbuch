package de.uniwue.dachs.haeuserbuch_backend.DTO.GeoJsonDTO;

import de.uniwue.dachs.haeuserbuch_backend.DTO.DistrictDTO;
import de.uniwue.dachs.haeuserbuch_backend.DTO.QuarterDTO;
import de.uniwue.dachs.haeuserbuch_backend.DTO.StreetDTO;
import de.uniwue.dachs.haeuserbuch_backend.model.Source;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class BuildingProperties extends Properties {
    private String name;
    private List<String> altNames = new ArrayList<>();
    private String houseNumber;
    private String currentHouseNumber;
    private StreetDTO currentStreet;
    private String partType;
    private String specialStatus;
    private QuarterDTO quarter;
    private DistrictDTO district;
    private String districtHouseNumber;
    private Set<Source> primarySources = new HashSet<>();
    private List<String> secondarySources = new ArrayList<>();
    private String internalNotes;
    private String generalNotes;
    private Date createdDate;
    private String createdBy;
    private Date lastModifiedDate;
    private String lastModifiedBy;
}
