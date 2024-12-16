package test.shop.domain.model.order;

import jakarta.persistence.*;
import lombok.Getter;
import test.shop.domain.*;
import test.shop.domain.model.delivery.Delivery;
import test.shop.domain.model.delivery.DeliveryStatus;
import test.shop.domain.model.member.Member;
import test.shop.application.dto.response.ItemDto;
import test.shop.application.dto.response.OrderDto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "orders")
@Getter
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    @Enumerated(EnumType.STRING)
    private OrderStatus status; //[ORDER, CANCEL]
    private int totalPrice;



    // ==연관관계 메서드== //
    public void saveMember(Member member) {
        this.member = member;
        member.addOrder(this);
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.saveOrder(this);
        recalculateTotalPrice(); // Recalculate total price whenever an order item is added

    }

    public void saveDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.saveOrder(this);
    }

    public void saveStatus(OrderStatus status) {
        this.status = status;
    }


   private void recalculateTotalPrice() {
        this.totalPrice = orderItems.stream().mapToInt(OrderItem::getTotalPrice).sum();
    }

    // ==생성 메서드== //
    public static Order createOrder(Member member, Delivery delivery, List<OrderItem> orderItems) {
        Order order = new Order();
        order.saveMember(member);
        order.saveDelivery(delivery);
        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }
        order.saveStatus(OrderStatus.CONFIRMED);
        return order;
    }
    public OrderDto toOrderDto() {
       OrderDto orderDto = new OrderDto();

       orderDto.setOrderId(this.getId());
       orderDto.setName(this.getMember().getUserId());
       orderDto.setStatus(this.getStatus());

       orderDto.setAddress(this.getDelivery().getAddress());
       orderDto.setCreatedDate(this.getCreatedDate());
       orderDto.setTotalPrice(this.getTotalPrice());
       orderDto.setOrderItems(this.getOrderItemsDtos());
       return orderDto;
    }


    // ==비즈니스 로직== //

    /**
     * 주문 취소
     */
    public void cancel() {
        if (delivery.getStatus() == DeliveryStatus.COMP) {
            throw new IllegalStateException("이미 배송완료된 상품은 취소가 불가능합니다");
        }
        for (OrderItem orderItem : orderItems) {
            orderItem.cancel();
        }
        recalculateTotalPrice(); // Recalculate total price after canceling order items
         this.saveStatus(OrderStatus.CANCELED);
    }
    // == Dtos == //
    public List<ItemDto> getOrderItemsDtos() {
        return orderItems.stream().map(OrderItem::toItemDto).collect(Collectors.toList());

    }

    /**
     * 전체 주문 가격 조회
     */
//    public int getTotalPrice() {
//        int totalPrice = 0;
//        for (OrderItem orderItem : orderItems) {
//            totalPrice += orderItem.getTotalPrice();
//        }
//        return totalPrice;
//    }


}
