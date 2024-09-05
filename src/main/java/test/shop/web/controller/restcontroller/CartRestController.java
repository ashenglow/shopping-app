package test.shop.web.controller.restcontroller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import test.shop.web.auth.AuthService;
import test.shop.web.dto.ItemDto;
import test.shop.web.dto.request.UpdateCartItemRequest;
import test.shop.web.dto.response.UpdateCartItemResponse;
import test.shop.web.service.CartService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "장바구니", description = "장바구니 API 입니다")
public class CartRestController {
    private final CartService cartService;
    private final AuthService authService;

   @RequestMapping("/api/auth/v1/mycart/{memberId}")
   @Operation(summary = "장바구니", description = "장바구니를 가져옵니다.")
    public ResponseEntity<List<ItemDto>> loadCart(@PathVariable("memberId") Long memberId) {
       List<ItemDto> cartItems = cartService.getCartItems(memberId);
       return ResponseEntity.ok(cartItems);
   }

    @PostMapping("/api/auth/v1/cart/{itemId}")
    @Operation(summary = "장바구니 상품 추가", description = "장바구니에 상품을 추가합니다.")
    public ResponseEntity<String> addToCart(HttpServletRequest request, @PathVariable("itemId") Long itemId, @RequestBody int count) throws JsonProcessingException {
        Long memberId = getMemberId(request);
        cartService.addItemToCart(itemId, memberId, count);
        return ResponseEntity.ok("Add item success");
    }

    @DeleteMapping("/api/auth/v1/cart/{itemId}")
    @Operation(summary = "장바구니 상품 삭제", description = "장바구니에서 상품을 삭제합니다.")
    public ResponseEntity<Long> deleteCartItem(HttpServletRequest request, @PathVariable("itemId") Long itemId) throws JsonProcessingException {
        Long memberId = getMemberId(request);
        Long removedItemId = cartService.deleteCartItem(itemId, memberId);
        return ResponseEntity.ok(removedItemId);
    }

    @PutMapping("/api/auth/v1/cart/update/{itemId}")
    @Operation(summary = "장바구니 상품 수량 변경", description = "장바구니에서 상품 수량을 변경합니다.")
    public ResponseEntity<UpdateCartItemResponse> updateCartItem(HttpServletRequest request, @PathVariable("itemId") Long itemId, @RequestBody UpdateCartItemRequest updateRequest) throws JsonProcessingException {
        Long memberId = getMemberId(request);
        Long updatedItemId = cartService.updateCartItem(itemId, memberId, updateRequest.getCount());
        UpdateCartItemResponse response = new UpdateCartItemResponse();
        response.setId(updatedItemId);
        response.setCount(updateRequest.getCount());
        return ResponseEntity.ok(response);
    }

    private Long getMemberId(HttpServletRequest request) throws JsonProcessingException {
        return authService.getMemberIdFromAccessToken(request);
    }

}
