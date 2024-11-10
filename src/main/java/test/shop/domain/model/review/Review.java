package test.shop.domain.model.review;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import test.shop.domain.model.item.Item;
import test.shop.domain.model.member.Member;
import test.shop.application.dto.request.ReviewDto;

@Entity
@Getter
@NoArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private double rating;
    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public Review(double rating, String comment) {
        this.rating = rating;
        this.comment = comment;
    }

    public void saveItem(Item item) {
        this.item = item;
        item.addReview(this);
    }

    public void saveMember(Member member) {
        this.member = member;
        member.addReview(this);
    }



public ReviewDto toReviewDto() {
        return ReviewDto.builder()
                .id(this.id)
                .productId(this.getItem().getId())
                .userId(this.getMember().getId())
                .username(this.getMember().getUsername())
                .userImg(this.getMember().getUserImg())
                .rating(this.getRating())
                .comment(this.getComment())
                .build();
}

}
