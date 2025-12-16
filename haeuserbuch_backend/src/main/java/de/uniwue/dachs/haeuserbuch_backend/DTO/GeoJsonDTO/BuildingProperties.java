package de.uniwue.dachs.haeuserbuch_backend.DTO.GeoJsonDTO;

import de.uniwue.dachs.haeuserbuch_backend.DTO.*;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class BuildingProperties extends Properties {
    private Set<BuildingNameDTO> names = new HashSet<>();
    private String houseNumber;
    private String currentHouseNumber;
    private Set<AddressDTO> addresses = new HashSet<>();
    private StreetDTO currentStreet;
    private String partType;
    private String specialStatus;
    private QuarterDTO quarter;
    private DistrictDTO district;
    private String districtHouseNumber;
    private Set<SourceDTO> primarySources = new HashSet<>();
    private Set<SourceDTO> secondarySources = new HashSet<>();
    private String internalNotes;
    private String generalNotes;
    private Date createdDate;
    private String createdBy;
    private Date lastModifiedDate;
    private String lastModifiedBy;
}
