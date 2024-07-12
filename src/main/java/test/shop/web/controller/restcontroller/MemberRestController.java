package test.shop.web.controller.restcontroller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import test.shop.domain.Member;
import test.shop.web.auth.AuthService;
import test.shop.web.auth.TokenUtil;
import test.shop.web.dto.ProfileDto;
import test.shop.web.dto.UserModelDto;
import test.shop.web.service.MemberService;

@RestController
@Slf4j
@RequiredArgsConstructor
public class MemberRestController {

private final TokenUtil tokenUtil;
    private final MemberService memberService;
    private final AuthService authService;

    @GetMapping("/api/auth/v1/member/update")
    public ResponseEntity<ProfileDto> getUpdateMemberProfile(@RequestBody Long id, HttpServletResponse response) {

        ProfileDto profile = memberService.getUpdateMemberProfile(id);
        return ResponseEntity.status(200).body(profile);
    }


    @PostMapping("/api/auth/v1/member/update")
    public ResponseEntity<Boolean> updateMemberProfile(@RequestBody ProfileDto form, HttpServletResponse response) {
        log.info("form : " + form.getId());
        memberService.update(form);
        return ResponseEntity.ok(true);
    }

    @RequestMapping("/api/auth/v1/member/{memberId}/delete")
    public ResponseEntity<String> deleteMember(@PathVariable("memberId") Long memberId) {
        memberService.delete(memberId);
        return ResponseEntity.ok("회원이 삭제되었습니다.");
    }
}
