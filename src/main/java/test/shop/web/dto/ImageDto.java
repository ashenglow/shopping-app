package test.shop.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ImageDto {

    private Long id;
    private String url;
    private Long productId;

    public ImageDto() {
    }
}
