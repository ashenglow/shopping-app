package test.shop.interfaces.rest.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import test.shop.application.dto.request.ProfileDto;
import test.shop.application.service.member.MemberService;
import test.shop.infrastructure.security.service.AuthService;

@RestController
@Slf4j
@RequiredArgsConstructor
@Tag(name = "회원", description = "회원 API 입니다")
public class MemberRestController {

    private final MemberService memberService;
    private final AuthService authService;

    @GetMapping("/api/auth/v1/member/update")
    @Operation(summary = "회원정보 수정 가져오기", description = "수정할 회원 정보를 가져옵니다.")
    public ResponseEntity<ProfileDto> getUpdateMemberProfile(HttpServletRequest request) {
        Long id = authService.getMemberIdFromAccessToken(request);
        ProfileDto profile = memberService.getUpdateMemberProfile(id);
        return ResponseEntity.status(200).body(profile);
    }


    @PostMapping("/api/auth/v1/member/update")
    @Operation(summary = "회원 정보 변경", description = "회원 정보를 변경합니다.")
    public ResponseEntity<Boolean> updateMemberProfile(@RequestBody ProfileDto form, HttpServletRequest request) {
        Long id = authService.getMemberIdFromAccessToken(request);
        if(id != null) {
            form.setId(id);
            memberService.update(form);
        }
        return ResponseEntity.ok(true);
    }

    @DeleteMapping ("/api/auth/v1/member/delete")
    @Operation(summary = "회원 삭제", description = "회원 정보를 삭제합니다.")
    public ResponseEntity<String> deleteMember(HttpServletRequest request) {
        Long id = authService.getMemberIdFromAccessToken(request);
        if(id != null) {
            memberService.delete(id);
        }
        return ResponseEntity.ok("회원이 삭제되었습니다.");
    }
}
