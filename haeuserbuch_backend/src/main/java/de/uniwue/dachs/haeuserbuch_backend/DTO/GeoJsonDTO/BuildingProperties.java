package de.uniwue.dachs.haeuserbuch_backend.DTO.GeoJsonDTO;

import de.uniwue.dachs.haeuserbuch_backend.DTO.*;
import de.uniwue.dachs.haeuserbuch_backend.DTO.PreviewDTO.FilePreviewDTO;
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
    private String propertyNumber;
    private String districtPropertyNumber;
    private Set<SourceDTO> sources = new HashSet<>();
    private Set<SourceDTO> literature = new HashSet<>();
    private Set<FilePreviewDTO> files = new HashSet<>();
    private String internalNotes;
    private String generalNotes;
    private Date createdDate;
    private String createdBy;
    private Date lastModifiedDate;
    private String lastModifiedBy;
}
