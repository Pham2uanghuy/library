package com.library.libraryapp.service;

import com.library.libraryapp.dto.AddressDTO;
import com.library.libraryapp.entity.PostalAddress;
import org.springframework.stereotype.Service;

import java.util.List;


public interface AddressService {
    AddressDTO createAddress(AddressDTO addressDTO);
    List<AddressDTO> getAllAddress();
    AddressDTO getAddressById(Long id);
    AddressDTO updateAddress(AddressDTO addressDTO);
    void deleteAddress(Long id);
    void updateAddressEntityFromAddressDTO(PostalAddress address, AddressDTO addressDTO);

}
