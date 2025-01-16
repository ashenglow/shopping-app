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
    private Long memberId;
    private String nickname;
    private String userImg;
    private double rating;
    private String comment;




}
