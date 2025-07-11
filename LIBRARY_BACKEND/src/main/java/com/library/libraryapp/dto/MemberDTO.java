package com.library.libraryapp.dto;

import com.library.libraryapp.entity.PostalAddress;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.time.LocalDate;

@Data
public class MemberDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private AddressDTO Address;
    private String email;
    private String phone;
    private String membershipStarted;
    private String membershipEnded;
    private Boolean isActive = true;
    private String barcodeNumber;
}
