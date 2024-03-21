package test.shop.web.form;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import test.shop.domain.Order;
import test.shop.domain.OrderItem;
import test.shop.domain.OrderStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class OrderForm {
    private Long orderId;
    private Long memberId;
    private String memberName;

    private OrderStatus orderStatus;

    private List<OrderItem> orderItems = new ArrayList<>();
    private String createdDate;

    @QueryProjection
    public OrderForm(Long orderId, Long memberId, String memberName, OrderStatus orderStatus, List<OrderItem> orderItems, String createdDate) {
        this.orderId = orderId;
        this.memberId = memberId;
        this.memberName = memberName;
        this.orderStatus = orderStatus;
        this.orderItems = orderItems;
        this.createdDate = createdDate;
    }

    public static List<OrderForm> createOrderForm(List<Order> result) {
        List<OrderForm> orderForms = new ArrayList<>();
        for (Order order : result) {
            orderForms.add(new OrderForm(order.getId(), order.getMember().getId(), order.getMember().getName(), order.getStatus(), order.getOrderItems(),
                    order.getCreatedDate()));
        }
        return orderForms;
    }
}
