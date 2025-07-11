package com.library.libraryapp.controller;


import com.library.libraryapp.dto.MemberDTO;
import com.library.libraryapp.service.MemberService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("api/members")
public class MemberController {

    private  MemberService memberService ;

    @PostMapping("addMember")
    // URL: http://localhost:8080/api/members/addMember
    public ResponseEntity<MemberDTO> addMember(@RequestBody MemberDTO memberDTO) {
        MemberDTO savedMember = memberService.addMember(memberDTO);
        return new ResponseEntity<>(savedMember, HttpStatus.CREATED);
    }

    @GetMapping("listAll")
    // URL: http://localhost:8080/api/members/listAll
    public ResponseEntity<List<MemberDTO>> listAll() {
        List<MemberDTO> allMembers = memberService.getAllMember();
        return new ResponseEntity<>(allMembers, HttpStatus.OK);
    }

    @GetMapping("{id}")
    //URL http://localhost:8080/api/member/1
    public ResponseEntity<MemberDTO> getBookById(@PathVariable("id") Long memberId) {
        MemberDTO MemberDTO = memberService.getMemberById(memberId);

        return new ResponseEntity<>(MemberDTO, HttpStatus.OK);
    }

    @PatchMapping("updateMember/{id}")
    // URL: http://localhost:8080/api/members/updateMember/1
    public ResponseEntity<MemberDTO> updateBook(@PathVariable("id") Long memberId, @RequestBody MemberDTO MemberDTO) {
        MemberDTO.setId(memberId);
        MemberDTO updatedMember  = memberService.updateMember(MemberDTO);
        return new ResponseEntity<>(updatedMember, HttpStatus.OK);
    }

    @DeleteMapping("deleteMember/{id}")
    public ResponseEntity<String> deleteMember(@PathVariable("id") Long memberId) {
        memberService.deleteMember(memberId);
        return new ResponseEntity<>("Successfully member deleted", HttpStatus.OK);
    }

    @GetMapping("search")
    public ResponseEntity<List<MemberDTO>> searchMember(
            @RequestParam(required = false) Long cardNumber,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestBody(required = false) String barcodeNumber) {
        List<MemberDTO> members = memberService.findMembersByCriteria(cardNumber, firstName, lastName, barcodeNumber);
        return new ResponseEntity<>(members, HttpStatus.OK);
    }
}
