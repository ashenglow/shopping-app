package test.shop.interfaces.rest.v1;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import test.shop.common.utils.Range;
import test.shop.common.utils.ResponseUtil;
import test.shop.common.utils.Result;
import test.shop.infrastructure.security.service.AuthService;
import test.shop.application.dto.response.ProductDetailDto;
import test.shop.application.dto.request.ProductDto;
import test.shop.application.service.item.ItemService;
import test.shop.application.service.review.ReviewService;

import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
@RequiredArgsConstructor
@Tag(name = "상품", description = "상품 API 입니다")
public class ItemRestController {

    private final ItemService itemService;
    private final ReviewService reviewService;
    private final AuthService authService;


    @PostMapping("/api/admin/v1/product/new")
    @Operation(summary = "상품 등록", description = "상품를 등록합니다.")
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
      @GetMapping("/api/public/v1/products")
    @Operation(summary = "상품 목록 조회", description = "상품 목록을 가져옵니다.")
    public ResponseEntity<Page<ProductDto>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(name = "category", required = false) String category,
            @RequestParam(name = "minPrice", defaultValue = "0") Integer minPrice,
            @RequestParam(name = "maxPrice", defaultValue = "25000") Integer maxPrice,
            @RequestParam(name = "ratings", defaultValue = "0") double ratings) {

        int size = 10;
        Range<Double> ratingsRange = ratings == 0 ?
                new Range<>(0.0, 5.0) : new Range<>(ratings, 5.0);
        Range<Integer> priceRange = new Range<>(minPrice, maxPrice);

        Page<ProductDto> itemsPage = itemService.findItems(page, size, priceRange, category, ratingsRange);
        //handling error
        if (itemsPage == null) {
            return ResponseEntity.notFound().build();
        }
          return ResponseUtil.getCacheableResponse(itemsPage)
                  .body(itemsPage);
    }


    /**
     * 상품 상세
     */
    @GetMapping("/api/public/v1/product/{itemId}")
    @Operation(summary = "상품 상세 조회", description = "상품 상세를 가져옵니다.")
    public ResponseEntity<Result<ProductDetailDto>> detail(@PathVariable("itemId") Long itemId) {
        ProductDetailDto dto = itemService.getItemDetail(itemId);
        Result<ProductDetailDto> result = new Result<>(dto);
        //handling error
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
       return ResponseUtil.getCacheableResponse(result)
               .body(result);
    }



    /**
     * 상품 삭제
     */
    @DeleteMapping("/api/admin/v1/product/{itemId}/delete")
    @Operation(summary = "상품 삭제", description = "상품을 삭제합니다.")
    public ResponseEntity<Boolean> deleteItem(@PathVariable("itemId") Long itemId) {
        itemService.deleteItem(itemId);
        return ResponseEntity.ok(true);
    }

    private Long getMemberId(HttpServletRequest request) throws JsonProcessingException {
        return authService.getMemberIdFromAccessToken(request);
    }

    private String getUserId(HttpServletRequest request) throws JsonProcessingException {
        return authService.getUserIdFromAccessToken(request);
    }


}
