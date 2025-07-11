package com.library.libraryapp.controller;

import com.library.libraryapp.dto.RegisterDTO;
import com.library.libraryapp.service.RegisterService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("api/registers")
public class RegistryController {

    private RegisterService registerService;

    @PostMapping("createRegister")
    //URL: http://localhost:8080/api/registers/createRegister
    public ResponseEntity<RegisterDTO> createRegister (@RequestBody RegisterDTO registerDTO) {
        RegisterDTO register = registerService.createRegister(registerDTO);

        return new ResponseEntity<>(register, HttpStatus.CREATED);
    }

    @GetMapping("listAll")
    //URL: http://localhost:8080/api/registers/listAll
    public ResponseEntity<List<RegisterDTO>> getAllRegisters() {
        List<RegisterDTO> registers = registerService.getAllRegisters();
        return new ResponseEntity<>(registers, HttpStatus.OK);
    }

    @GetMapping("{id}")
    //URL: http://localhost:8080/api/registers/1
    public ResponseEntity<RegisterDTO> getRegisterById(@PathVariable("id") Long id) {
        RegisterDTO register = registerService.getRegisterById(id);
        return new ResponseEntity<>(register, HttpStatus.OK);
    }

    @PatchMapping("updateRegister/{id}")
    // URL: http://localhost:8080/api/register/updateRegister/2
    public ResponseEntity<RegisterDTO> updateRegisterById(@PathVariable("id") Long id, @RequestBody RegisterDTO registerDTO) {
        registerDTO.setId(id);
        RegisterDTO updatedRegister = registerService.updateRegister(registerDTO);
        return new ResponseEntity<>(updatedRegister, HttpStatus.OK);
    }

    @DeleteMapping("deleteRegister/{id}")
    // URL: http://localhost:8080/api/reisters/deleteRegister/2
    public ResponseEntity<String> deleteRegisterById(@PathVariable("id") Long id) {
        registerService.deleteRegister(id);
        return new ResponseEntity<>("checkout register deleted successfully", HttpStatus.OK);
    }

    @GetMapping("member/{memberId}")
    // URL:http://localhost:8080/api/register/member/1
    public ResponseEntity<List<RegisterDTO>> getAllRegistersByMemberId(@PathVariable("memberId") Long memberId) {

        List<RegisterDTO> registerDTOS = registerService.getRegisterByMemberId(memberId);
        return new ResponseEntity<>(registerDTOS, HttpStatus.OK);
    }

    @GetMapping("book/{bookId}")
    //URL: http://localhost:8080/api/registers/book/1
    public ResponseEntity<List<RegisterDTO>> getAllRegistersByBookId(@PathVariable("bookId") Long bookId) {
        List<RegisterDTO> registerDTOS = registerService.getRegisterByBookId(bookId);
        return new ResponseEntity<>(registerDTOS, HttpStatus.OK);
    }



}
