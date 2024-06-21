package test.shop.web.auth.dto;

import lombok.*;

@Getter
@AllArgsConstructor
public class TokenResDto {
    private final String accessToken;
private final String refreshToken;


}
