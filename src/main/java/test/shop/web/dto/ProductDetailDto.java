package test.shop.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import test.shop.domain.item.Category;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
public class ProductDetailDto {
    private Long id;
    private String name;
    private Integer price;
    private Integer stockQuantity;
    private String description;
    private Integer ratings;
    private Integer numOfReviews;
    private Category category;
    private List<ReviewDto> reviews;
    private List<ImageDto> images;

    public ProductDetailDto() {


    }
}
