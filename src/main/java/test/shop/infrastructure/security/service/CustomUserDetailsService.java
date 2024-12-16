package test.shop.infrastructure.security.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import test.shop.domain.model.member.Member;
import test.shop.application.dto.response.MemberAuthDto;
import test.shop.domain.repository.MemberRepository;
import test.shop.infrastructure.security.CustomUserDetails;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;


    @Override
    public UserDetails loadUserByUsername(String id) {
        Member member = memberRepository.findMemberById(Long.parseLong(id))
                .orElseThrow(() -> new UsernameNotFoundException("Member not found"));
        log.info("loadUserByUsername: {}", member);
        MemberAuthDto authDto = new MemberAuthDto();
        MemberAuthDto dto = authDto.MembertoAuthDto(member);
        return new CustomUserDetails(dto);

    }


}
