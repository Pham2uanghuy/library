package com.library.libraryapp.service.impl;

import com.library.libraryapp.dto.RegisterDTO;
import com.library.libraryapp.entity.CheckoutRegister;
import com.library.libraryapp.mapper.RegisterMapper;
import com.library.libraryapp.repository.CheckoutRegisterRepository;
import com.library.libraryapp.service.RegisterService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegisterServiceImpl implements RegisterService {
    @Value("${library.loanPeriodInDays}")
    private int loanPeriodInDays;

    @Value("${library.overdueFineRate}")
    private Double overdueFineRate;
    private final RegisterMapper registerMapper;
    private final CheckoutRegisterRepository checkoutRegisterRepository;
    @Override
    public RegisterDTO createRegister(RegisterDTO registerDTO) {
        CheckoutRegister checkoutRegister = registerMapper.mapToCheckoutRegisterEntity(registerDTO);

        //calculate due date
        LocalDate dueDate = calculateDueDate(checkoutRegister.getCheckoutDate());
        checkoutRegister.setDueDate(dueDate);

        checkoutRegister = checkoutRegisterRepository.save(checkoutRegister);

        return registerMapper.mapToRegisterDTO(checkoutRegister);
    }

    @Override
    public List<RegisterDTO> getAllRegisters() {
        List<CheckoutRegister> checkoutRegisters= checkoutRegisterRepository.findAll();
        return checkoutRegisters.stream().map(registerMapper::mapToRegisterDTO).collect(Collectors.toList());
    }

    @Override
    public RegisterDTO getRegisterById(Long id) {
        Optional<CheckoutRegister> checkoutRegister = checkoutRegisterRepository.findById(id);
        CheckoutRegister checkoutRegisterEntity = checkoutRegister.get();
        return registerMapper.mapToRegisterDTO(checkoutRegisterEntity);
    }

    @Override
    public RegisterDTO updateRegister(RegisterDTO registerDTO) {
        // find existing register by id
        Optional<CheckoutRegister>checkoutRegisterOptional=checkoutRegisterRepository.findById(registerDTO.getId());
        CheckoutRegister checkoutRegisterToUpdate=checkoutRegisterOptional.get();

        // do partial update
        updateCheckoutRegisterFromDTO(checkoutRegisterToUpdate, registerDTO);


        // calculate overdue fine
        calculateOverdueFine(checkoutRegisterToUpdate);

        // save updated register to DB
        CheckoutRegister updatedCheckoutRegister = checkoutRegisterRepository.save(checkoutRegisterToUpdate);
        // return register DTO using mapper
        return registerMapper.mapToRegisterDTO(updatedCheckoutRegister);
    }

    @Override
    public void deleteRegister(Long id) {
        checkoutRegisterRepository.deleteById(id);
    }

    @Override
    public List<RegisterDTO> getRegisterByMemberId(Long memberId) {
        List<CheckoutRegister> checkoutRegisters = checkoutRegisterRepository.findByMemberId(memberId);
        return checkoutRegisters.stream().map(registerMapper::mapToRegisterDTO).collect(Collectors.toList());
    }

    @Override
    public List<RegisterDTO> getRegisterByBookId(Long bookId) {
        List<CheckoutRegister> checkoutRegisters = checkoutRegisterRepository.findByBookId(bookId);
        return checkoutRegisters.stream().map(registerMapper::mapToRegisterDTO).collect(Collectors.toList());
    }

    private void updateCheckoutRegisterFromDTO(CheckoutRegister checkoutRegisterToUpdate, RegisterDTO registerDTO) {
         // the agent can either prolong the book or record the return of the book
        if (registerDTO.getDueDate()!=null) {
            checkoutRegisterToUpdate.setDueDate(LocalDate.parse(registerDTO.getDueDate()));
        }
        if (registerDTO.getReturnDate()!=null) {
            checkoutRegisterToUpdate.setReturnDate(LocalDate.parse(registerDTO.getReturnDate()));
        }

    }

    private LocalDate calculateDueDate(LocalDate checkoutDate) {
        return checkoutDate.plusDays(loanPeriodInDays);
    }

    private void calculateOverdueFine(CheckoutRegister checkoutRegister) {
        if (checkoutRegister.getReturnDate()!=null
            && checkoutRegister.getReturnDate().isAfter(checkoutRegister.getDueDate())) {
            long daysOverdue = ChronoUnit.DAYS.between(checkoutRegister.getDueDate(), checkoutRegister.getReturnDate());
            //overdue fine = daysOverdue * overdueFineRate
            double overdueFine = daysOverdue * overdueFineRate;
            checkoutRegister.setOverdueFine(overdueFine);
        }

    }
}
