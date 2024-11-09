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
    private final ItemService itemService;
    private final MemberService memberService;
    private final ReviewRepository reviewRepository;

    @Transactional(readOnly = false)
    public void saveReview(ReviewDto form) {

        Member member = memberService.findMemberById(form.getUserId());
        Item item = itemService.findOne(form.getProductId());
        Review review = buildReviewFromDto(form, member, item);
        reviewRepository.save(review);
       }

       private Review buildReviewFromDto(ReviewDto dto, Member member, Item item) {
           Review review = Review.builder()
                   .rating(dto.getRating())
                   .comment(dto.getComment())
                   .build();
           review.saveMember(member);
           review.saveItem(item);
           return review;

       }


       @Transactional(readOnly = false)
       public void deleteReview(Long reviewId) {
           reviewRepository.deleteById(reviewId);
       }
       public List<ReviewDto> findReviews(Long itemId) {
           List<Review> reviews = reviewRepository.findByItemId(itemId)
                   .orElseThrow(() -> new EntityNotFoundException("review doesn't exist"));
           return reviews.stream().map(Review::toReviewDto).collect(Collectors.toList());
       }

}
