package test.shop.application.dto.response;

import lombok.Data;
import test.shop.domain.model.order.OrderStatus;
import test.shop.domain.value.Address;

import java.util.List;

@Data
public class OrderDto {
    private Long orderId;
    private String name;
    private OrderStatus status;
    private Address address;
    private List<ItemDto> orderItems;
    private String createdDate;
    private int totalPrice;



}
