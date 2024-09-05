package test.shop.web.controller.restcontroller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import test.shop.web.auth.AuthService;
import test.shop.web.auth.TokenUtil;
import test.shop.web.dto.ProfileDto;
import test.shop.web.service.MemberService;

@RestController
@Slf4j
@RequiredArgsConstructor
@Tag(name = "회원", description = "회원 API 입니다")
public class MemberRestController {

    private final MemberService memberService;

    @GetMapping("/api/auth/v1/member/update")
    @Operation(summary = "회원정보 수정 가져오기", description = "수정할 회원 정보를 가져옵니다.")
    public ResponseEntity<ProfileDto> getUpdateMemberProfile(@RequestBody Long id, HttpServletResponse response) {

        ProfileDto profile = memberService.getUpdateMemberProfile(id);
        return ResponseEntity.status(200).body(profile);
    }


    @PostMapping("/api/auth/v1/member/update")
    @Operation(summary = "회원 정보 변경", description = "회원 정보를 변경합니다.")
    public ResponseEntity<Boolean> updateMemberProfile(@RequestBody ProfileDto form, HttpServletResponse response) {
        log.info("form : " + form.getId());
        memberService.update(form);
        return ResponseEntity.ok(true);
    }

    @DeleteMapping ("/api/auth/v1/member/{memberId}/delete")
    @Operation(summary = "회원 삭제", description = "회원 정보를 삭제합니다.")
    public ResponseEntity<String> deleteMember(@PathVariable("memberId") Long memberId) {
        memberService.delete(memberId);
        return ResponseEntity.ok("회원이 삭제되었습니다.");
    }
}
