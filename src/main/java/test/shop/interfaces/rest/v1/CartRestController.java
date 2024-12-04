package test.shop.interfaces.rest.v1;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import test.shop.infrastructure.security.service.AuthService;
import test.shop.application.dto.response.ItemDto;
import test.shop.application.dto.request.UpdateCartItemRequest;
import test.shop.application.dto.response.UpdateCartItemResponse;
import test.shop.application.service.cart.CartService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "장바구니", description = "장바구니 API 입니다")
public class CartRestController {
    private final CartService cartService;
    private final AuthService authService;

   @GetMapping("/api/auth/v1/mycart")
   @Operation(summary = "장바구니 조회", description = "장바구니를 가져옵니다.")
    public ResponseEntity<List<ItemDto>> loadCart(HttpServletRequest request) {
       Long memberId = authService.getMemberIdFromAccessToken(request);
       List<ItemDto> cartItems = cartService.getCartItems(memberId);
       return ResponseEntity.ok(cartItems);
   }

    @PostMapping("/api/auth/v1/cart/{itemId}")
    @Operation(summary = "장바구니 상품 추가", description = "장바구니에 상품을 추가합니다.")
    public ResponseEntity<String> addToCart(HttpServletRequest request, @PathVariable("itemId") Long itemId, @RequestBody int count) {
        Long memberId = authService.getMemberIdFromAccessToken(request);
        cartService.addItemToCart(itemId, memberId, count);
        return ResponseEntity.ok("Add item success");
    }

    @DeleteMapping("/api/auth/v1/cart")
    @Operation(summary = "장바구니 상품 삭제", description = "장바구니에서 상품을 삭제합니다.")
    public ResponseEntity<List<Long>> deleteCartItems(HttpServletRequest request, @RequestBody List<Long> itemIds){
        Long memberId = authService.getMemberIdFromAccessToken(request);
        List<Long> removedItemIds = cartService.deleteCartItems(itemIds, memberId);
        return ResponseEntity.ok(removedItemIds);
    }

    @PutMapping("/api/auth/v1/cart/update/{itemId}")
    @Operation(summary = "장바구니 상품 수량 변경", description = "장바구니에서 상품 수량을 변경합니다.")
    public ResponseEntity<UpdateCartItemResponse> updateCartItem(HttpServletRequest request, @PathVariable("itemId") Long itemId, @RequestBody UpdateCartItemRequest updateRequest) {
        Long memberId = authService.getMemberIdFromAccessToken(request);
        Long updatedItemId = cartService.updateCartItem(itemId, memberId, updateRequest.getCount());
        UpdateCartItemResponse response = new UpdateCartItemResponse();
        response.setId(updatedItemId);
        response.setCount(updateRequest.getCount());
        return ResponseEntity.ok(response);
    }


}
