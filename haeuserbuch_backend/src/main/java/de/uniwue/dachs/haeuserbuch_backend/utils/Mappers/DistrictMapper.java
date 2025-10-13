package de.uniwue.dachs.haeuserbuch_backend.utils.Mappers;

import de.uniwue.dachs.haeuserbuch_backend.DTO.DistrictDTO;
import de.uniwue.dachs.haeuserbuch_backend.model.District;
import de.uniwue.dachs.haeuserbuch_backend.repository.DistrictRepository;
import org.springframework.stereotype.Component;

@Component
public class DistrictMapper {
    private final DistrictRepository districtRepository;

    public DistrictMapper(DistrictRepository districtRepository) {
        this.districtRepository = districtRepository;
    }

    public District DistrictDTOToDistrict(DistrictDTO districtDTO) {
        if (districtDTO == null || districtDTO.getId() == null) {
            return null;
        }
        return districtRepository.findById(districtDTO.getId()).orElse(null);
    }

    public DistrictDTO DistrictToDTO(District district) {
        if (district == null) {
            return null;
        }
        DistrictDTO districtDTO = new DistrictDTO();
        districtDTO.setId(district.getId());
        districtDTO.setName(district.getName());
        return districtDTO;
    }
}
