package test.shop.domain;

import lombok.Data;

@Data
public class OrderSearchCond {
    private String memberName;
    private OrderStatus orderStatus;

    }
