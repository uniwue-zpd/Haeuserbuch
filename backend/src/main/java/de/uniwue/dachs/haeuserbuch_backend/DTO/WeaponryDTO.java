package de.uniwue.dachs.haeuserbuch_backend.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WeaponryDTO {
    private Long id;
    private WeaponDTO weapon;
    private String originalText;
}
