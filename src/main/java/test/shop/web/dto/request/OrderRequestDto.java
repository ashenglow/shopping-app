package test.shop.web.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderRequestDto implements UnifiedRequestDto{

    private Long itemId;
    private int count;
    private int price;
}
