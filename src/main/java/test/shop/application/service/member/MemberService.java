package test.shop.application.service.member;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import test.shop.application.dto.request.MemberJoinRequestDto;
import test.shop.domain.model.cart.Cart;
import test.shop.domain.model.member.Member;
import test.shop.application.dto.request.ProfileDto;
import test.shop.domain.repository.CartRepository;
import test.shop.domain.repository.MemberRepository;
import test.shop.domain.value.Address;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {


    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;

    /**
     *
     * 회원가입
     */
    @Transactional //변경
    public Long join(MemberJoinRequestDto dto) {
        validateDuplicateMember(dto);
        Member member = buildMemberFromDto(dto);
        memberRepository.save(member);
        //새 카트 생성
        Cart cart = new Cart();
        cart.saveMember(member);
        cartRepository.save(cart);
        return member.getId();
    }

    private void validateDuplicateMember(MemberJoinRequestDto dto) {
        boolean validate = memberRepository.findMemberByUserId(dto.getUserId())
                .isEmpty();
        if (!validate) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    private Member buildMemberFromDto(MemberJoinRequestDto dto){
        return Member.builder()
                .userId(dto.getUserId())
                .nickname(dto.getNickname())
                .password(dto.getPassword())
                .email(dto.getEmail())
                .memberType(dto.getMemberType())
                .userImg(dto.getUserImg())
                .provider(dto.getProvider())
                .providerId(dto.getProviderId())
                .address(dto.getAddress())
                .build();
    }

    /**
     * 전체 회원 조회
     *
     */
//    public List<MemberDto> findMembers() {
//        memberRepository.findAll().stream().map(Member::newMemberDto).collect(Collectors.toList());
//    }

    public Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("member doesn't exist"));
    }

    public Optional<Member> findOne(Long memberId) {
        return memberRepository.findById(memberId);
    }

    public ProfileDto getUpdateMemberProfile(Long memberId) {
        Member member = memberRepository.findMemberById(memberId).orElseThrow(() -> new UsernameNotFoundException("Member not found"));
        return member.toProfileDto();


    }



    @Transactional
    public void update(ProfileDto dto) {
        Member foundMember = memberRepository.findMemberByUserId(dto.getUserId())
                .orElseThrow(() -> new UsernameNotFoundException("Member not found"));
        foundMember.updateMember(dto.getPassword(), dto.getNickname(), dto.getAddress(), dto.getEmail(), dto.getUserImg());
    }

    @Transactional
    public void updateMemberAddress(Long memberId, Address address) {
        Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));
        member.updateAddress(address);
        memberRepository.save(member);
    }
    @Transactional
    public void delete(Long memberId) {
        memberRepository.deleteById(memberId);
    }
}
