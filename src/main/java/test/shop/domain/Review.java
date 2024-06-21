package test.shop.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import test.shop.domain.item.Item;
import test.shop.web.dto.ReviewDto;

@Entity
@Getter
@NoArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int rating;
    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public Review(int rating, String comment) {
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

    public static Review createReview(int rating, String comment, Item item, Member member) {
        Review review = new Review(rating, comment);
        review.saveItem(item);
        review.saveMember(member);
        return review;
    }



    public static ReviewDto newReviewDto(Review review) {
        ReviewDto reviewDto = new ReviewDto();
        reviewDto.setId(review.getId());
        reviewDto.setUsername(review.getMember().getUsername());
        reviewDto.setUserId(review.getMember().getId());
        reviewDto.setProductId(review.getItem().getId());
        reviewDto.setUserImg(review.getMember().getUserImg());
        reviewDto.setRating(review.getRating());
        reviewDto.setComment(review.getComment());
        return reviewDto;
    }
}
