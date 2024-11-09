package test.shop.infrastructure.security.jwt;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import test.shop.domain.model.member.MemberType;

import java.io.Serializable;

@Getter
public class TokenSubject implements Serializable {
    private final Long memberId;
    private final String username;
    private final MemberType memberType;

@JsonCreator
    public TokenSubject(@JsonProperty("memberId") Long memberId, @JsonProperty("username") String username, @JsonProperty("memberType") MemberType memberType) {
        this.memberId = memberId;
        this.username = username;
        this.memberType = memberType;
    }

    public static TokenSubject userTokenSubject(Long memberId, String username) {
        return new TokenSubject(memberId, username, MemberType.USER);
    }

    public static TokenSubject adminTokenSubject(Long memberId, String username) {
        return new TokenSubject(memberId, username, MemberType.ADMIN);
    }
}
