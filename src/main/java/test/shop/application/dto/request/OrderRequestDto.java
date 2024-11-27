package test.shop.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class OrderRequestDto implements UnifiedRequestDto{

    private Long itemId;
    private int count;
    private int price;
}
