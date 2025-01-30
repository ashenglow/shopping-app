package test.shop.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import test.shop.domain.value.Address;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class OrderRequestDto implements UnifiedRequestDto{
    private List<OrderItemDto> orderItems;
    private Address shippingAddress;
}
