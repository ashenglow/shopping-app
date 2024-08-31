package test.shop.web.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import test.shop.domain.Cart;
import test.shop.domain.Member;
import test.shop.web.dto.ProfileDto;
import test.shop.web.dto.UserModelDto;
import test.shop.web.repository.CartRepository;
import test.shop.web.repository.MemberRepository;

import java.util.List;
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
    public Long join(Member member) {
        validateDuplicateMember(member); //중복회원 검증
        memberRepository.save(member);
        //새 카트 생성
        Cart cart = new Cart();
        cart.saveMember(member);
        cartRepository.save(cart);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        boolean validate = memberRepository.findMemberByUsername(member.getUsername())
                .isEmpty();
        if (!validate) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
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
        return member.toProfileDto(memberId);


    }



    @Transactional
    public void update(ProfileDto dto) {
        Member foundMember = memberRepository.findMemberByUsername(dto.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Member not found"));
        foundMember.updateMember(dto.getPassword(), dto.getUsername(), dto.getAddress());
    }

    @Transactional
    public void delete(Long memberId) {
        memberRepository.deleteById(memberId);
    }
}
