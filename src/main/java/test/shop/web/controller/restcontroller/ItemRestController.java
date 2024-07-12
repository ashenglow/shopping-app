package test.shop.web.controller.restcontroller;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import test.shop.utils.Range;
import test.shop.utils.Result;
import test.shop.web.auth.AuthService;
import test.shop.web.dto.ProductDetailDto;
import test.shop.web.dto.ProductDto;
import test.shop.web.dto.ReviewDto;
import test.shop.web.service.ItemService;
import test.shop.web.service.ReviewService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ItemRestController {

    private final ItemService itemService;
    private final ReviewService reviewService;
    private final AuthService authService;


    @RequestMapping("/api/admin/v1/product/new")
    public ResponseEntity<String> create(@RequestBody ProductDto form) {
        itemService.saveItem(form);
        //handling error
        if (form == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body("상품이 등록되었습니다.");
    }

    /**
     * 상품 목록
     */
    @RequestMapping("/api/public/v1/all-products")
    public ResponseEntity<Result<List<ProductDto>>> list() {
        List<ProductDto> productDtos = itemService.findAll();
        //handling error
        if (productDtos == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new Result<>(productDtos));
    }

    /**
     * 상품 상세
     */
    @RequestMapping("/api/public/v1/product/{itemId}")
    public ResponseEntity<Result<ProductDetailDto>> detail(@PathVariable("itemId") Long itemId) {
        ProductDetailDto dto = itemService.getItemDetail(itemId);
        //handling error
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new Result<>(dto));
    }

    /**
     * 상품 리뷰 작성
     */
    @RequestMapping("/api/v1/product/{itemId}/review")
    public ResponseEntity<Boolean> createReview(HttpServletRequest request, @RequestBody ReviewDto form, @PathVariable("itemId") Long itemId) throws JsonProcessingException {
        Long memberId = getMemberId(request);
        form.setUserId(memberId);
        String username = getUsername(request);
        form.setUsername(username);
        reviewService.saveReview(form, itemId);
        return ResponseEntity.status(HttpStatus.CREATED).body(true);
    }

    /**
     * 상품 리뷰 삭제
     */
    @DeleteMapping("/api/v1/review/delete")
    public ResponseEntity<Boolean> deleteReview(@RequestParam("id") Long reviewId) {
        reviewService.deleteReview(reviewId);
        //handling error
        if (reviewId == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(true);
    }

    /**
     * 전체 상품 리뷰 조회
     */
    @GetMapping("/api/public/v1/reviews")
    public ResponseEntity<Result<List<ReviewDto>>> listReviews(@RequestParam("id") Long itemId) {
        List<ReviewDto> dtos = reviewService.findReviews(itemId);
        //handling error
        if (dtos == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new Result<>(dtos));
    }

    @GetMapping("/api/public/v1/products")
    public ResponseEntity<Page<ProductDto>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(name = "category", required = false) String category,
            @RequestParam(name = "minPrice", defaultValue = "0") Integer minPrice,
            @RequestParam(name = "maxPrice", defaultValue = "25000") Integer maxPrice,
            @RequestParam(name = "ratings", defaultValue = "0") int ratings) {

        int size = 10;
        Range<Integer> range = new Range<>(minPrice, maxPrice);
        Page<ProductDto> itemsPage = itemService.findItems(page, size, range, category, ratings);
        //handling error
        if (itemsPage == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(itemsPage);
    }


    /**
     * 상품 수정
     */

//    @RequestMapping("/api/v1/admin/product/{itemId}")
//    public ResponseEntity<String> updateItem(@RequestBody ItemDto form, @PathVariable("itemId") Long itemId){
//        Item item = ItemMapper.INSTANCE.toEntity(form);
//        itemService.updateItem(item, itemId);
//        return ResponseEntity.ok("상품이 수정되었습니다.");
//    }

    /**
     * 상품 삭제
     */
    @RequestMapping("/api/admin/v1/product/{itemId}/delete")
    public ResponseEntity<String> deleteItem(@PathVariable("itemId") Long itemId) {
        itemService.deleteItem(itemId);
        return ResponseEntity.ok("상품이 삭제되었습니다.");
    }

    private Long getMemberId(HttpServletRequest request) throws JsonProcessingException {
        return authService.getMemberIdFromAccessToken(request);
    }

    private String getUsername(HttpServletRequest request) throws JsonProcessingException {
        return authService.getUsernameFromAccessToken(request);
    }


}
