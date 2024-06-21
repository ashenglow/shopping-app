package test.shop.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ItemDto{
    private Long id;
    private String name;
    private int price;
    private List<ImageDto> images;
    private int stockQuantity;
    private int count;

    public ItemDto() {
    }
}
