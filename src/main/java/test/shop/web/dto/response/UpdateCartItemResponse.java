package test.shop.web.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateCartItemResponse {
    private Long id;
    private int count;
}
