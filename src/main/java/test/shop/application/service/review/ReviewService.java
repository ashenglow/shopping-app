package test.shop.application.service.review;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import test.shop.application.service.item.ItemService;
import test.shop.application.service.member.MemberService;
import test.shop.domain.model.member.Member;
import test.shop.domain.model.review.Review;
import test.shop.domain.model.item.Item;
import test.shop.application.dto.request.ReviewDto;
import test.shop.domain.repository.ItemRepository;
import test.shop.domain.repository.MemberRepository;
import test.shop.domain.repository.ReviewRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewService {
    private final ItemRepository itemRepository;
    private final MemberService memberService;
    private final ReviewRepository reviewRepository;

    @Transactional(readOnly = false)
    public void saveReview(ReviewDto form) {

        Member member = memberService.findMemberById(form.getUserId());

        Item item = itemRepository.findItemById(form.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Item not found"));

        Review review = buildReviewFromDto(form, member, item);

        // set associations
        review.saveMember(member);
        review.saveItem(item);

        // save review
        Review savedReview = reviewRepository.save(review);

        // update item statistics
        item.updateReviewStats();
        itemRepository.save(item);
    }

       private Review buildReviewFromDto(ReviewDto dto, Member member, Item item) {
           return Review.builder()
                   .rating(dto.getRating())
                   .comment(dto.getComment())
                   .build();
       }


       @Transactional(readOnly = false)
       public void deleteReview(Long reviewId) {
           Review review = reviewRepository.findById(reviewId)
                   .orElseThrow(() -> new EntityNotFoundException("Review not found"));
            // remove review in Item
           Item item = review.getItem();

           // remove associations
           review.saveItem(null);
           review.saveMember(null);

           // delete review
           reviewRepository.delete(review);

           // update item statistics
           item.updateReviewStats();
           itemRepository.save(item);
       }
       public List<ReviewDto> findReviews(Long itemId) {
           List<Review> reviews = reviewRepository.findByItemId(itemId)
                   .orElseThrow(() -> new EntityNotFoundException("review doesn't exist"));
           return reviews.stream().map(Review::toReviewDto).collect(Collectors.toList());
       }

}
