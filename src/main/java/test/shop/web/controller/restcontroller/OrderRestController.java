package test.shop.web.controller.restcontroller;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import test.shop.web.auth.AuthService;
import test.shop.web.dto.OrderDto;
import test.shop.web.dto.ItemDto;
import test.shop.web.dto.request.OrderRequestDto;
import test.shop.web.service.OrderService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class OrderRestController {

    private final OrderService orderService;
    private final AuthService authService;

    @RequestMapping("/api/auth/v1/order/new/{memberId}")
    public ResponseEntity<Long> order(@PathVariable("memberId") String memberId, @RequestBody List<OrderRequestDto> dtos) {
        Long parsedId = Long.parseLong(memberId);
        Long orderId = orderService.order(parsedId, dtos);
        return ResponseEntity.ok(orderId);
    }
//    @RequestMapping("/api/auth/v1/order/new/{memberId}")
//    public ResponseEntity<Long> order(@PathVariable("memberId") Long memberId, @RequestBody List<OrderRequestDto> dtos) {
//
//        Long orderId = orderService.order(memberId, dtos);
//        return ResponseEntity.ok(orderId);
//    }

    @RequestMapping("/api/auth/v1/order/{orderId}")
    public ResponseEntity<OrderDto> getOrderDetails(@PathVariable("orderId") Long orderId) {
        OrderDto order = orderService.findOrderById(orderId);
        return ResponseEntity.ok(order);
    }

//    @RequestMapping("/api/v1/admin/orders")
//    public List<Order> orderList(OrderSearchCond orderSearchCond, int offset, int limit) {
//        return orderService.findOrders(orderSearchCond, offset, limit);
//
//    }

    @RequestMapping("/api/auth/v1/orders/me")
    public Page<OrderDto> myOrders(HttpServletRequest request, @RequestParam(value = "offset", defaultValue = "0") int offset, @RequestParam(value = "limit", defaultValue = "100") int limit) throws JsonProcessingException {
        Long memberId = getMemberId(request);
        return orderService.findOrdersByMemberId(memberId, offset, limit);

    }

    @RequestMapping("/api/auth/v1/order/{orderId}/cancel")
    public void cancelOrder(@PathVariable("orderId") Long orderId) {
        orderService.cancelOrder(orderId);
    }
     private Long getMemberId(HttpServletRequest request) throws JsonProcessingException {
        return authService.getMemberIdFromAccessToken(request);
    }

}
