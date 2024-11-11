package test.shop.interfaces.rest.v1;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import test.shop.application.dto.request.ReviewDto;
import test.shop.application.service.review.ReviewService;
import test.shop.utils.Result;

import java.util.List;
@RestController
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "Review management API")
public class ReviewRestController {

    private final ReviewService reviewService;
    /**
     * 상품 리뷰 작성
     */
    @PutMapping("/api/auth/v1/product/{itemId}/review")
    @Operation(summary = "상품 리뷰 작성", description = "상품 리뷰를 작성합니다.")
    public ResponseEntity<Boolean> createReview(HttpServletRequest request, @RequestBody ReviewDto form, @PathVariable("itemId") Long itemId) throws JsonProcessingException {
        reviewService.saveReview(form);
        return ResponseEntity.status(HttpStatus.CREATED).body(true);
    }

    /**
     * 상품 리뷰 삭제
     */
    @DeleteMapping("/api/auth/v1/review/delete")
    @Operation(summary = "상품 리뷰 삭제", description = "상품 리뷰를 삭제합니다.")
    public ResponseEntity<Boolean> deleteReview(@RequestParam("id") Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.ok(true);
    }

    /**
     * 전체 상품 리뷰 조회
     */
    @GetMapping("/api/public/v1/reviews")
    @Operation(summary = "상품 리뷰 전체 조회", description = "상품 리뷰 전체를 가져옵니다.")
    public ResponseEntity<Result<List<ReviewDto>>> listReviews(@RequestParam("id") Long itemId) {
        List<ReviewDto> dtos = reviewService.findReviews(itemId);
        //handling error
        if (dtos == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new Result<>(dtos));
    }

}
