package test.shop.application.dto.response;

import lombok.*;

@Getter
@AllArgsConstructor
public class TokenResDto {
    private final String accessToken;
private final String refreshToken;


}
