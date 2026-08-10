package de.uniwue.dachs.haeuserbuch_backend.utils.Mappers;

import de.uniwue.dachs.haeuserbuch_backend.DTO.StreetDTO;
import de.uniwue.dachs.haeuserbuch_backend.model.Street;
import de.uniwue.dachs.haeuserbuch_backend.repository.StreetRepository;
import org.springframework.stereotype.Component;

@Component
public class StreetMapper {
    private final StreetRepository streetRepository;

    public StreetMapper(StreetRepository streetRepository) {
        this.streetRepository = streetRepository;
    }

    public Street StreetDTOToStreet(StreetDTO streetDTO) {
        if (streetDTO == null || streetDTO.getId() == null) {
            return null;
        }
        return streetRepository.findById(streetDTO.getId()).orElse(null);
    }

    public StreetDTO StreetToDTO(Street street) {
        if (street == null) {
            return null;
        }
        StreetDTO streetDTO = new StreetDTO();
        streetDTO.setId(street.getId());
        streetDTO.setName(street.getName());
        return streetDTO;
    }
}
