package com.library.libraryapp.mapper;

import com.library.libraryapp.dto.RegisterDTO;
import com.library.libraryapp.entity.Book;
import com.library.libraryapp.entity.CheckoutRegister;
import com.library.libraryapp.entity.Member;
import com.library.libraryapp.repository.BookRepository;
import com.library.libraryapp.repository.MemberRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@AllArgsConstructor
@Component
public class RegisterMapper {

    private MemberRepository memberRepository;
    private BookRepository bookRepository;

    // map entity to DTO
    public RegisterDTO mapToRegisterDTO(CheckoutRegister checkoutRegister) {
        RegisterDTO dto = new RegisterDTO();
        dto.setId(checkoutRegister.getId());
        dto.setMemberId(checkoutRegister.getMember().getId());
        dto.setBookId(checkoutRegister.getBook().getId());


        // convert date to string
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        dto.setCheckoutDate(checkoutRegister.getCheckoutDate().format(formatter));
        dto.setDueDate(checkoutRegister.getDueDate().format(formatter));
        if (checkoutRegister.getReturnDate()!=null) {
            dto.setReturnDate(checkoutRegister.getReturnDate().format(formatter));
        }

        dto.setOverdueFine(checkoutRegister.getOverdueFine());

        return dto;
    }

    //map dto to enity
    public CheckoutRegister mapToCheckoutRegisterEntity(RegisterDTO dto) {
        CheckoutRegister checkoutRegister = new CheckoutRegister();
        checkoutRegister.setId(dto.getId());

        // fetch the member by id
        Member member = memberRepository.findById(dto.getMemberId()).get();
        checkoutRegister.setMember(member);

        Book book = bookRepository.findById(dto.getBookId()).get();
        checkoutRegister.setBook(book);

        // parse date

        checkoutRegister.setCheckoutDate(LocalDate.parse(dto.getCheckoutDate()));
        if (dto.getDueDate()!=null) {
            checkoutRegister.setDueDate(LocalDate.parse(dto.getDueDate()));
        }
        if (dto.getReturnDate()!=null) {
            checkoutRegister.setReturnDate(LocalDate.parse(dto.getReturnDate()));
        }
//        checkoutRegister.setReturnDate(LocalDate.parse(dto.getReturnDate()));
        checkoutRegister.setOverdueFine(dto.getOverdueFine());


        return checkoutRegister;
    }
}
