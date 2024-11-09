package test.shop.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ItemDto{
    private Long id;
    private String name;
    private int price;
    private List<ResponseImageDto> images;
    private int stockQuantity;
    private int count;

    public ItemDto() {
    }
}
