package test.shop.application.dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDto {
    private Long id;
    private Long productId;
    private Long userId;
    private String username;
    private String userImg;
    private int rating;
    private String comment;




}
