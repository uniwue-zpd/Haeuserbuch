package de.uniwue.dachs.haeuserbuch_backend.utils.Mappers;

import de.uniwue.dachs.haeuserbuch_backend.DTO.AddressDTO;
import de.uniwue.dachs.haeuserbuch_backend.model.Address;
import de.uniwue.dachs.haeuserbuch_backend.repository.AddressRepository;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AddressMapper {
    private final AddressRepository addressRepository;
    private final StreetMapper streetMapper;

    public AddressMapper(AddressRepository addressRepository, StreetMapper streetMapper) {
        this.addressRepository = addressRepository;
        this.streetMapper = streetMapper;
    }

    public Address AddressDTOToAddress(AddressDTO addressDTO) {
        if (addressDTO == null) return null;
        if (addressDTO.getId() == null) {
            Address address = new Address();
            address.setStreet(streetMapper.StreetDTOToStreet(addressDTO.getStreet()));
            address.setHouseNumber(addressDTO.getHouseNumber());
            address.setFromDate(addressDTO.getFromDate());
            address.setToDate(addressDTO.getToDate());
            return address;
        } else {
            return addressRepository.findById(addressDTO.getId()).orElse(null);
        }
    }

    public Set<Address> AddressDTOsToAddresses(Set<AddressDTO> addressDTOs) {
        if (addressDTOs == null) return null;
        return addressDTOs.stream().map(this::AddressDTOToAddress).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    public AddressDTO AddressToDTO(Address address) {
        if (address == null) return null;
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setId(address.getId());
        addressDTO.setStreet(streetMapper.StreetToDTO(address.getStreet()));
        addressDTO.setHouseNumber(address.getHouseNumber());
        addressDTO.setFromDate(address.getFromDate());
        addressDTO.setToDate(address.getToDate());
        return addressDTO;
    }

    public Set<AddressDTO> AddressesToDTOs(Set<Address> addresses) {
        if (addresses == null) return null;
        return addresses.stream().map(this::AddressToDTO).filter(Objects::nonNull).collect(Collectors.toSet());
    }
}
