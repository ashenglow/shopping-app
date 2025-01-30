package test.shop.interfaces.rest.v1;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import test.shop.infrastructure.security.service.AuthService;
import test.shop.application.dto.response.OrderDto;
import test.shop.application.dto.request.OrderRequestDto;
import test.shop.application.service.order.OrderService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@Tag(name = "주문", description = "주문 API 입니다")
public class OrderRestController {

    private final OrderService orderService;
    private final AuthService authService;

    @PostMapping("/api/auth/v1/order/new")
    @Operation(summary = "주문 생성", description = "주문을 생성합니다.")
    public ResponseEntity<Long> order(@RequestBody OrderRequestDto orderRequest, HttpServletRequest request) {
        Long memberId = authService.getMemberIdFromAccessToken(request);
        Long orderId = orderService.order(memberId, orderRequest);
        return ResponseEntity.ok(orderId);
    }

    @GetMapping("/api/auth/v1/order/{orderId}")
    @Operation(summary = "주문 상세 조회", description = "주문 상세를 가져옵니다.")
    public ResponseEntity<OrderDto> getOrderDetails(@PathVariable("orderId") Long orderId) {
        OrderDto order = orderService.findOrderById(orderId);
        return ResponseEntity.ok(order);
    }


    @GetMapping("/api/auth/v1/orders/me")
    @Operation(summary = "주문 목록 조회", description = "회원 주문 목록을 가져옵니다.")
    public Page<OrderDto> myOrders(HttpServletRequest request, @RequestParam(value = "offset", defaultValue = "0") int offset, @RequestParam(value = "limit", defaultValue = "100") int limit) {
        Long memberId = authService.getMemberIdFromAccessToken(request);
        return orderService.findOrdersByMemberId(memberId, offset, limit);

    }

    @DeleteMapping("/api/auth/v1/order/{orderId}/cancel")
    @Operation(summary = "주문 취소", description = "주문을 취소합니다.")
    public ResponseEntity<Boolean> cancelOrder(@PathVariable("orderId") Long orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.ok(true);
    }

}
