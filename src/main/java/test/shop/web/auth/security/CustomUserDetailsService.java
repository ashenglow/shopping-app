package test.shop.web.auth.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import test.shop.domain.Member;
import test.shop.web.auth.security.CustomUserDetails;
import test.shop.web.dto.MemberAuthDto;
import test.shop.web.repository.MemberRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;


    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        Member member = memberRepository.findMemberById(Long.parseLong(id))
                .orElseThrow(() -> new UsernameNotFoundException("Member not found"));
        log.info("loadUserByUsername: {}", member);
        MemberAuthDto authDto = new MemberAuthDto();
        MemberAuthDto dto = authDto.MembertoAuthDto(member);
        return new CustomUserDetails(dto);

    }


}
