package com.library.libraryapp.mapper;

import com.library.libraryapp.dto.MemberDTO;
import com.library.libraryapp.entity.Member;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MemberMapper {

    public static MemberDTO mapToMemberDTO(Member member) {
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setId(member.getId());
        memberDTO.setFirstName(member.getFirstName());
        memberDTO.setLastName(member.getLastName());
        memberDTO.setEmail(member.getEmail());
        memberDTO.setPhone(member.getPhone());

        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        if (member.getDateOfBirth() != null) {
            memberDTO.setDateOfBirth(LocalDate.parse(member.getDateOfBirth().format(formatter)));
        }

        if (member.getPostalAddress() != null) {
            memberDTO.setAddress(AddressMapper.mapToAddressDTO(member.getPostalAddress()));
        }

        memberDTO.setIsActive(member.getIsActive());
        if (member.getMembershipStarted() != null) {
            memberDTO.setMembershipStarted(member.getMembershipStarted().format(formatter));
        }
        if (member.getMembershipEnded() != null) {
            memberDTO.setMembershipEnded(member.getMembershipEnded().format(formatter));
        }
        memberDTO.setBarcodeNumber(member.getBarcodeNumber());
        return memberDTO;
    }

    public static Member mapToMemberEnity(MemberDTO memberDTO) {
        Member member = new Member();
        member.setId(memberDTO.getId());
        member.setFirstName(memberDTO.getFirstName());
        member.setLastName(memberDTO.getLastName());
        member.setEmail(memberDTO.getEmail());
        member.setPhone(memberDTO.getPhone());

        member.setDateOfBirth(LocalDate.parse(memberDTO.getDateOfBirth().format(DateTimeFormatter.ISO_LOCAL_DATE)));
        member.setIsActive(memberDTO.getIsActive());
        member.setMembershipStarted(LocalDate.parse(memberDTO.getMembershipStarted(), DateTimeFormatter.ISO_LOCAL_DATE));
        if (memberDTO.getMembershipEnded() != null) {
            member.setMembershipEnded(LocalDate.parse(memberDTO.getMembershipEnded(), DateTimeFormatter.ISO_LOCAL_DATE));
        }
        member.setBarcodeNumber(memberDTO.getBarcodeNumber());
        if (memberDTO.getAddress() != null) {
            member.setPostalAddress(AddressMapper.mapToAddressEntity(memberDTO.getAddress()));
        }
        return member;
    }
}
