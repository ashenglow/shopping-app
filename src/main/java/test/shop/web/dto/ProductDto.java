package test.shop.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import test.shop.domain.item.Category;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ProductDto{

    private Long id;
    private String name;
    private int price;
    private int stockQuantity;
    private String description;
    private int ratings;
    private int numOfReviews;
    private Category category;
    private List<ImageDto> images;

    public ProductDto() {
    }

}
