package test.shop.web.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import test.shop.domain.Member;
import test.shop.domain.Review;
import test.shop.domain.item.Item;
import test.shop.web.dto.ReviewDto;
import test.shop.web.repository.ItemRepository;
import test.shop.web.repository.MemberRepository;
import test.shop.web.repository.ReviewRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewService {
    private final ItemRepository itemRepository;
    private final MemberRepository  memberRepository;
    private final ReviewRepository reviewRepository;

    @Transactional(readOnly = false)
    public void saveReview(ReviewDto form, Long itemId) {
        Long userId = form.getUserId();
           Member member = memberRepository.findMemberById(userId)
                   .orElseThrow(() -> new UsernameNotFoundException("Member not found"));
           Item item = itemRepository.findById(itemId).orElseThrow(() -> new IllegalArgumentException("item doesn't exist"));
           Review review = Review.createReview(form.getRating(), form.getComment(), item, member);
           reviewRepository.save(review);
       }

       @Transactional(readOnly = false)
       public void deleteReview(Long reviewId) {
           reviewRepository.deleteById(reviewId);
       }
       public List<ReviewDto> findReviews(Long itemId) {
           Item item = itemRepository.findById(itemId).orElseThrow(() -> new IllegalArgumentException("item doesn't exist"));
           List<Review> reviews = reviewRepository.findByItemId(itemId)
                   .orElseThrow(() -> new EntityNotFoundException("review doesn't exist"));
           return reviews.stream().map(Review::newReviewDto).collect(Collectors.toList());
       }

}
