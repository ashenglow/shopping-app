package test.shop.domain.model.order;

import lombok.Data;

@Data
public class OrderSearchCond {
    private String memberName;
    private OrderStatus orderStatus;

    }
