package de.uniwue.dachs.haeuserbuch_backend.DTO.GeoJsonDTO;

import de.uniwue.dachs.haeuserbuch_backend.DTO.*;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class BuildingProperties extends Properties {
    private Long year;
    private Long parcelNumber;
    private Long parcelNumberCounter;
    private Set<BuildingNameDTO> names = new HashSet<>();
    private Set<AddressDTO> addresses = new HashSet<>();
    private String partType;
    private String object;
    private QuarterDTO quarter;
    private DistrictDTO district;
    private String houseNumber;
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
