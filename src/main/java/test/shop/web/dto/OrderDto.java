package test.shop.web.dto;

import com.querydsl.core.annotations.QueryProjection;
import jakarta.persistence.*;
import lombok.Data;
import test.shop.domain.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
