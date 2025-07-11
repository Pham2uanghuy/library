package com.library.libraryapp.controller;

import com.library.libraryapp.dto.AddressDTO;
import com.library.libraryapp.dto.BookDTO;
import com.library.libraryapp.entity.Book;
import com.library.libraryapp.entity.PostalAddress;
import com.library.libraryapp.mapper.AddressMapper;
import com.library.libraryapp.mapper.BookMapper;
import com.library.libraryapp.repository.AddressRepository;
import com.library.libraryapp.service.AddressService;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.amqp.RabbitConnectionDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("api/addresses")
public class AddressController {

    private final AddressService addressService;
    private final AddressRepository addressRepository;

    @PostMapping("createAddress")
    // http://localhost:8080/api/addresses/createAddress
    public ResponseEntity<AddressDTO> addAddress(@RequestBody AddressDTO addressDTO) {
        addressService.createAddress(addressDTO);
        return new ResponseEntity<>(addressDTO, HttpStatus.CREATED);
    }

    @GetMapping("listAll")
    public ResponseEntity<List<AddressDTO>> getAllAddresses() {
        List<AddressDTO> addresses = addressService.getAllAddress();
        return new ResponseEntity<>(addresses, HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<AddressDTO> getAddressById(@PathVariable long id) {
        AddressDTO addressDTO = addressService.getAddressById(id);
        return new ResponseEntity<>(addressDTO, HttpStatus.OK);
    }

    @PatchMapping("updateAddress/{id}")
    public  ResponseEntity<AddressDTO> updateAddress(@PathVariable long id, @RequestBody AddressDTO addressDTO) {
        addressDTO.setId(id);
        AddressDTO updatedAddress = addressService.updateAddress(addressDTO);
        return new ResponseEntity<>(updatedAddress, HttpStatus.OK);
    }


    @DeleteMapping("deleteAddress/{id}")
    public ResponseEntity<String> deleteAddress(@PathVariable("id") Long id) {
        addressService.deleteAddress(id);
        return new ResponseEntity<>("Address successfully deleted", HttpStatus.OK);
    }
}
