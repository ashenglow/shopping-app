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
        // remove from old item if exists
        if(this.item != null){
            this.item.getReviews().remove(this);
        }
        this.item = item;
        //add to new item's reviews if not already there
        if(item != null && !item.getReviews().contains(this)){
            item.getReviews().add(this);
        }
    }

    public void saveMember(Member member) {
        // remove from old member if exists
        if(this.member != null){
            this.member.getReviews().remove(this);
        }
        this.member = member;
        // add to new member's reviews if not already there
        if (member != null && !member.getReviews().contains(this)) {
            member.getReviews().add(this);
        }
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
