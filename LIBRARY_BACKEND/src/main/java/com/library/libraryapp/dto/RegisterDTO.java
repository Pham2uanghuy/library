package com.library.libraryapp.dto;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
public class RegisterDTO {
    private Long id;
    private Long memberId;
    private Long bookId;
    private String checkoutDate;
    private String dueDate;
    private String returnDate;
    private Double overdueFine;
}
