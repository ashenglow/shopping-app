package test.shop.web.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ReviewDto {
    private Long id;
    private String username;
    private Long userId;
    private String userImg;
    private int rating;
    private String comment;
    private Long productId;

    public ReviewDto() {
    }


}
