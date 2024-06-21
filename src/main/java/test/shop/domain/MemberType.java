package test.shop.domain;

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
