package test.shop.infrastructure.security.token;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenData {
    private String uuid;
    private String userId;

}
