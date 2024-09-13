package test.shop.web.dto;

import lombok.*;
import test.shop.domain.item.Category;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto{

    private Long id;
    private String name;
    private Integer price;
    private Integer stockQuantity;
    private String description;
    private Integer ratings;
    private Integer numOfReviews;
    private Category category;
    private List<ImageDto> images;

    public ProductDto(Long id, String name, Integer price, Integer stockQuantity, Integer ratings, Integer numOfReviews, Category category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.ratings = ratings;
        this.numOfReviews = numOfReviews;
        this.category = category;
        this.images = new ArrayList<>();
    }
}
