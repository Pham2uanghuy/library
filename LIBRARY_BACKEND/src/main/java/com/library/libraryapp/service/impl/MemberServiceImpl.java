package com.library.libraryapp.service.impl;

import com.library.libraryapp.dto.AddressDTO;
import com.library.libraryapp.dto.MemberDTO;
import com.library.libraryapp.dto.MemberDTO;
import com.library.libraryapp.entity.Book;
import com.library.libraryapp.entity.Member;
import com.library.libraryapp.entity.Member;
import com.library.libraryapp.entity.PostalAddress;
import com.library.libraryapp.mapper.AddressMapper;
import com.library.libraryapp.mapper.BookMapper;
import com.library.libraryapp.mapper.MemberMapper;
import com.library.libraryapp.mapper.MemberMapper;
import com.library.libraryapp.repository.AddressRepository;
import com.library.libraryapp.repository.MemberRepository;
import com.library.libraryapp.repository.MemberRepository;
import com.library.libraryapp.service.AddressService;
import com.library.libraryapp.service.MemberService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.hibernate.metamodel.internal.MemberResolver;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Service
@AllArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final AddressService addressService;
    private AddressRepository addressRepository;
    private MemberRepository memberRepository;
    private EntityManager entityManager;

    @Transactional
    @Override
    public MemberDTO addMember(MemberDTO memberDTO) {
        // deal with postal_address
        PostalAddress postalAddress = new PostalAddress();
        AddressDTO addressDTO = memberDTO.getAddress();
        if (addressDTO!=null) {
            postalAddress = AddressMapper.mapToAddressEntity(addressDTO);
            addressRepository.save(postalAddress);
        }

        // convert memberDTO to member entity
        Member member = MemberMapper.mapToMemberEnity(memberDTO);

        member.setPostalAddress(postalAddress);
        member = memberRepository.save(member);
        return MemberMapper.mapToMemberDTO(member);
    }


    public List<MemberDTO> getAllMember() {
        List<Member> members = memberRepository.findAll();
        return members.stream().map(MemberMapper::mapToMemberDTO).collect(Collectors.toList());
    }




    public  MemberDTO getMemberById(Long memberId) {

        Optional<Member> optionalMember = memberRepository.findById(memberId);
        Member member = optionalMember.get();
        return MemberMapper.mapToMemberDTO(member);
    }

    
    public MemberDTO updateMember(MemberDTO memberDTO) {
        // find existing member by id
        Optional<Member> optionalMember = memberRepository.findById(memberDTO.getId());
        
        // do partial update of the member
        Member member = optionalMember.get();
        updateMemberEntityFromDTO(member, memberDTO);
        
        member = memberRepository.save(member);
        return MemberMapper.mapToMemberDTO(member);
    }

    @Override
    public void deleteMember(Long id) {
        memberRepository.deleteById(id);
    }

    @Override
    public List<MemberDTO> findMembersByCriteria(Long id, String firstName, String lastName, String barcodeNumber) {
        CriteriaBuilder cb  = entityManager.getCriteriaBuilder();
        CriteriaQuery<Member> cq = cb.createQuery(Member.class);
        Root<Member> member = cq.from(Member.class);
        List<Predicate> predicates = new ArrayList<>();
        if (id != null) {
            predicates.add(cb.equal(member.get("id"), id));
        }
        if (firstName != null && !firstName.isEmpty()) {
            predicates.add(cb.like(cb.lower(member.get("firstName")), "%" + firstName.toLowerCase() + "%"));
        }
        if (lastName != null && !lastName.isEmpty()) {
            predicates.add(cb.like(cb.lower(member.get("lastName")), "%" + lastName.toLowerCase() + "%"));
        }
        if (barcodeNumber != null && !barcodeNumber.isEmpty()) {
            predicates.add(cb.like(cb.lower(member.get("barcodeNumber")), "%" + barcodeNumber.toLowerCase() + "%"));
        }
        cq.where(cb.and(predicates.toArray(new Predicate[0])));
        List<Member> result = entityManager.createQuery(cq).getResultList();
        return result.stream().map(MemberMapper::mapToMemberDTO).collect(Collectors.toList());
    }

    private void updateMemberEntityFromDTO(Member memberToUpdate, MemberDTO memberDTO) {
        if (memberDTO.getId() != null) {
            memberToUpdate.setId(memberDTO.getId());
        }
        if (memberDTO.getEmail() != null) {
            memberToUpdate.setEmail(memberDTO.getEmail());
        }
        if (memberDTO.getFirstName() != null) {
            memberToUpdate.setFirstName(memberDTO.getFirstName());
        }
        if (memberDTO.getLastName() != null) {
            memberToUpdate.setLastName(memberDTO.getLastName());
        }
        if (memberDTO.getDateOfBirth() != null) {
            memberToUpdate.setDateOfBirth(memberDTO.getDateOfBirth());
        }
        if (memberDTO.getIsActive() != null) {
            memberToUpdate.setIsActive(memberDTO.getIsActive());
        }
        if (memberDTO.getPhone() != null) {
            memberToUpdate.setPhone(memberDTO.getPhone());
        }
        if (memberDTO.getMembershipStarted() != null) {
            memberToUpdate.setMembershipStarted(LocalDate.parse(memberDTO.getMembershipStarted()));
        }
        if (memberDTO.getBarcodeNumber() != null) {
            memberToUpdate.setBarcodeNumber(memberDTO.getBarcodeNumber());
        }

        // the member is active if membershipEnded = null
//        if (memberDTO.getMembershipEnded() != null) {
//            if (memberDTO.getMembershipEnded().isEmpty()) {
//                memberToUpdate.setMembershipEnded(null);
//                memberToUpdate.setIsActive(true);
//            }
//            else {
//                memberToUpdate.setMembershipEnded(LocalDate.parse(memberDTO.getMembershipEnded()));
//                memberToUpdate.setIsActive(false);
//            }
//        }

        if (memberDTO.getMembershipEnded() != null) {
            if (memberDTO.getMembershipEnded().isEmpty()) {
                memberToUpdate.setMembershipEnded(null);
                if (memberDTO.getIsActive() == null) { // nếu không truyền isActive, mặc định là active
                    memberToUpdate.setIsActive(true);
                }
            } else {
                memberToUpdate.setMembershipEnded(LocalDate.parse(memberDTO.getMembershipEnded()));
                memberToUpdate.setIsActive(false);
            }
        }
        //updating the address
        if (memberDTO.getAddress() != null) {
            // if the member alreadt has the address, update it
            // otherwise create a new PostalAddress entity
            PostalAddress addressToUpdate;
            if (memberToUpdate.getPostalAddress() != null) {
                addressToUpdate = memberToUpdate.getPostalAddress();
            } else {
                addressToUpdate = new PostalAddress();
            }
            addressService.updateAddressEntityFromAddressDTO(addressToUpdate, memberDTO.getAddress());
            addressRepository.save(addressToUpdate);
            // to update PostalAddress entity, we will use existing adress service
            memberToUpdate.setPostalAddress(addressToUpdate);


        }
    }
}
