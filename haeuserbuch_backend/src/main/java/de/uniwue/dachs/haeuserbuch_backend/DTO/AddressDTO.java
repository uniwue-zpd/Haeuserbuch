package de.uniwue.dachs.haeuserbuch_backend.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressDTO {
    private Long id;
    private StreetDTO street;
    private String houseNumber;
    private String fromDate;
    private String toDate;
}
