package test.shop.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import test.shop.domain.model.item.Category;
import test.shop.application.dto.response.ResponseImageDto;

import java.util.ArrayList;
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
    private double ratings;
    private int numOfReviews;
    private Category category;
    private String thumbnailUrl;
    @Builder.Default
    private List<ResponseImageDto> images = new ArrayList<>();

    public void addImage(String url) {
        if (images == null) {
            images = new ArrayList<>();
        }
        images.add(ResponseImageDto.builder()
                .url(url)
                .build());
    }

    public ProductDto() {
        this.images = new ArrayList<>();
    }

}
