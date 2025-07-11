package com.library.libraryapp.service.impl;

import com.library.libraryapp.dto.AddressDTO;
import com.library.libraryapp.entity.PostalAddress;
import com.library.libraryapp.mapper.AddressMapper;
import com.library.libraryapp.repository.AddressRepository;
import com.library.libraryapp.service.AddressService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AddressServiceImpl implements AddressService {

    private AddressRepository addressRepository;

    @Override
    public AddressDTO createAddress(AddressDTO addressDTO) {
        PostalAddress address = AddressMapper.mapToAddressEntity(addressDTO);
        addressRepository.save(address);
        return AddressMapper.mapToAddressDTO(address);
    }

    @Override
    public List<AddressDTO> getAllAddress() {
        List<PostalAddress> addresses = addressRepository.findAll();
        return addresses.stream().map(AddressMapper::mapToAddressDTO).collect(Collectors.toList());
    }

    @Override
    public AddressDTO getAddressById(Long id) {
        Optional<PostalAddress> addresses = addressRepository.findById(id);
        PostalAddress address = addresses.get();
        return AddressMapper.mapToAddressDTO(address);
    }

    @Override
    public AddressDTO updateAddress(AddressDTO addressDTO) {
        // find existing book by id
        Optional<PostalAddress> addresses = addressRepository.findById(addressDTO.getId());
        // do partial update of the book ( update only non-null fields)
        PostalAddress addressToUpdate = addresses.get();
        updateAddressEntityFromAddressDTO(addressToUpdate, addressDTO);

        // save updated book to db
        PostalAddress savedAddress = addressRepository.save(addressToUpdate);
        // return bookDTO using mapper
        return AddressMapper.mapToAddressDTO(savedAddress);
    }

    public void updateAddressEntityFromAddressDTO(PostalAddress address, AddressDTO addressDTO) {
        if (addressDTO.getId()!=null) {
            address.setId(addressDTO.getId());
        }
        if (addressDTO.getPlaceName()!=null) {
            address.setPlaceName(addressDTO.getPlaceName());
        }
        if (addressDTO.getStreetName()!=null) {
            address.setStreetName(addressDTO.getStreetName());
        }
        if (addressDTO.getStreetNumber()!=null) {
            address.setStreetNumber(addressDTO.getStreetNumber());
        }
        if (addressDTO.getCountry()!=null) {
            address.setCountry(addressDTO.getCountry());
        }
        if (addressDTO.getZipCode()!=null) {
            address.setZipCode(addressDTO.getZipCode());
        }
        if (addressDTO.getAdditionalInfo()!=null) {
            address.setAdditionalInfo(addressDTO.getAdditionalInfo());
        }

    }

    @Override
    public void deleteAddress(Long id) {
        addressRepository.deleteById(id);
    }


}
