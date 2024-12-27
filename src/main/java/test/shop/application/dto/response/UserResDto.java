package test.shop.application.dto.response;

import lombok.Getter;
import test.shop.domain.model.member.Member;

@Getter
public class UserResDto {

    private final Long memberId;
    private final String userId;

    public UserResDto(Long memberId, String userId) {
        this.memberId = memberId;
        this.userId = userId;
    }

    public static UserResDto of(Member member) {
        return new UserResDto(
                member.getId(),
                member.getUserId()
        );
    }
}
