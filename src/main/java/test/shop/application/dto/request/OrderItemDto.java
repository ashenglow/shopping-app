package test.shop.application.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderItemDto {
    private Long itemId;
    private int count;
    private int price;
    private String name; // for caching
    private String thumbnailUrl; // for caching
}
