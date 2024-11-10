package test.shop.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import test.shop.application.dto.request.ReviewDto;
import test.shop.domain.model.item.Category;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
public class ProductDetailDto {
    private Long id;
    private String name;
    private int price;
    private int stockQuantity;
    private String description;
    private double ratings;
    private int numOfReviews;
    private Category category;
    private List<ReviewDto> reviews;
    private List<ResponseImageDto> images;

    public ProductDetailDto() {
    }
}
