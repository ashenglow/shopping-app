package test.shop.infrastructure.security.jwt;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import test.shop.domain.model.member.MemberType;

import java.io.Serializable;

@Getter
public class TokenSubject implements Serializable {
    private final Long memberId;
    private final String userId;
    private final MemberType memberType;

@JsonCreator
    public TokenSubject(@JsonProperty("memberId") Long memberId, @JsonProperty("userId") String userId, @JsonProperty("memberType") MemberType memberType) {
        this.memberId = memberId;
        this.userId = userId;
        this.memberType = memberType;
    }

    public static TokenSubject userTokenSubject(Long memberId, String userId) {
        return new TokenSubject(memberId, userId, MemberType.USER);
    }

    public static TokenSubject adminTokenSubject(Long memberId, String userId) {
        return new TokenSubject(memberId, userId, MemberType.ADMIN);
    }
}
