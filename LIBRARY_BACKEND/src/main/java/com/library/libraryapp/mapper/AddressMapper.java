package com.library.libraryapp.mapper;

import com.library.libraryapp.dto.AddressDTO;
import com.library.libraryapp.entity.PostalAddress;

public class AddressMapper {
    // map entity to dto
    public  static AddressDTO mapToAddressDTO(PostalAddress address) {
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setId(address.getId());
        addressDTO.setCountry(address.getCountry());
        addressDTO.setPlaceName(address.getPlaceName());
        addressDTO.setZipCode(address.getZipCode());
        addressDTO.setStreetNumber(address.getStreetNumber());
        addressDTO.setStreetName(address.getStreetName());
        addressDTO.setAdditionalInfo(address.getAdditionalInfo());
        return addressDTO;
    }
    // map dto to entity
    public  static PostalAddress mapToAddressEntity(AddressDTO addressDTO) {
        PostalAddress postalAddress = new PostalAddress();
        postalAddress.setId(addressDTO.getId());
        postalAddress.setCountry(addressDTO.getCountry());
        postalAddress.setPlaceName(addressDTO.getPlaceName());
        postalAddress.setZipCode(addressDTO.getZipCode());
        postalAddress.setStreetNumber(addressDTO.getStreetNumber());
        postalAddress.setStreetName(addressDTO.getStreetName());
        postalAddress.setAdditionalInfo(addressDTO.getAdditionalInfo());
        return postalAddress;
    }
}
