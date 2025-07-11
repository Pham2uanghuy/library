package com.library.libraryapp.service;

import com.library.libraryapp.dto.MemberDTO;
import org.springframework.stereotype.Service;

import java.util.List;

public interface MemberService {
    MemberDTO addMember(MemberDTO memberDTO);

    List<MemberDTO> getAllMember();

    MemberDTO getMemberById(Long id);

    MemberDTO updateMember(MemberDTO memberDTO);

    void deleteMember(Long id);

    List<MemberDTO> findMembersByCriteria(Long id, String firstName, String lastName, String barcodeNumber);
}
