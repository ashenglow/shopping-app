package test.shop.web.form;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import test.shop.domain.OrderItem;
import test.shop.domain.OrderStatus;

import java.util.List;

@Data
public class OrderForm {
    private Long orderId;
    private Long memberId;
    private String memberName;

    private OrderStatus orderStatus;

    private List<OrderItem> orderItems;

    @QueryProjection
    public OrderForm(Long orderId, Long memberId, String memberName, OrderStatus orderStatus, List<OrderItem> orderItems) {
        this.orderId = orderId;
        this.memberId = memberId;
        this.memberName = memberName;
        this.orderStatus = orderStatus;
        this.orderItems = orderItems;
    }
}
