package test.shop.domain.model.member;

import lombok.Getter;
@Getter
public enum MemberType {
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER");
    private final String roleName;

    MemberType(String roleName) {
        this.roleName = roleName;
    }


}
