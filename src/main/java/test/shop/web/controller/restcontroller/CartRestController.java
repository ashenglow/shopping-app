package test.shop.web.controller.restcontroller;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import test.shop.domain.Cart;
import test.shop.web.auth.AuthService;
import test.shop.web.dto.ItemDto;
import test.shop.web.dto.request.CartRequestDto;
import test.shop.web.service.CartService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CartRestController {
    private final CartService cartService;
    private final AuthService authService;

   @RequestMapping("/api/auth/v1/mycart/{memberId}")
    public ResponseEntity<List<ItemDto>> loadCart(@PathVariable("memberId") Long memberId) {
       List<ItemDto> cartItems = cartService.getCartItems(memberId);
       return ResponseEntity.ok(cartItems);
   }

    @PostMapping("/api/auth/v1/cart/{itemId}")
    public ResponseEntity<String> addToCart(HttpServletRequest request, @PathVariable("itemId") Long itemId, @RequestBody int count) throws JsonProcessingException {
        Long memberId = getMemberId(request);
        cartService.addItemToCart(itemId, memberId, count);
        return ResponseEntity.ok("Add item success");
    }

    @DeleteMapping("/api/auth/v1/cart/{itemId}")
    public ResponseEntity<Long> deleteCartItem(HttpServletRequest request, @PathVariable("itemId") Long itemId) throws JsonProcessingException {
        Long memberId = getMemberId(request);
        Long removedItemId = cartService.deleteCartItem(itemId, memberId);
        return ResponseEntity.ok(removedItemId);
    }

    @PutMapping("/api/auth/v1/cart/update/{itemId}")
    public ResponseEntity<Long> updateCartItem(HttpServletRequest request, @PathVariable("itemId") Long itemId, @RequestBody UpdateCartItemRequest updateRequest) throws JsonProcessingException {
        Long memberId = getMemberId(request);
        Long updatedItemId = cartService.updateCartItem(itemId, memberId, updateRequest.getCount());
        return ResponseEntity.ok(updatedItemId);
    }

    private Long getMemberId(HttpServletRequest request) throws JsonProcessingException {
        return authService.getMemberIdFromAccessToken(request);
    }

    @Getter
    public class UpdateCartItemRequest {
    private int count;

    // getter and setter for count
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
}
