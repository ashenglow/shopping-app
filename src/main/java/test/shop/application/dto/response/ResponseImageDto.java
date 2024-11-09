package test.shop.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseImageDto {

    private Long id;
    private String url;
    private int displayOrder;
    private Long productId;


}
