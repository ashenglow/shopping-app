package test.shop.domain;

import jakarta.persistence.*;
import lombok.Getter;
import test.shop.domain.item.Item;

@Entity
@Table(name = "order_item")
@Getter
public class OrderItem {

    private int orderPrice; //주문 가격
    private int count; //주문 수량

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item; //주문 상품

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order; //주문

    public void saveOrder(Order order) {
        this.order = order;
    }

    public void saveItem(Item item) {
        this.item = item;
    }

    public void saveOrderPrice(int orderPrice) {
        this.orderPrice = orderPrice;
    }

    public void saveCount(int count) {
        this.count = count;
    }

    //==생성 메서드=//
    public static OrderItem createOrderItem(Item item, int orderPrice, int count) {
        OrderItem orderItem = new OrderItem();
        orderItem.saveItem(item);
        orderItem.saveOrderPrice(orderPrice);
        orderItem.saveCount(count);
        item.removeStock(count);
        return orderItem;
    }
    //==비즈니스 로직==//

    /**
     * 주문 취소
     */
    public void cancel() {
     getItem().addStock(count);
    }
    //==조회 로직==//

    /**
     * 주문상품 전체 가격 조회
     */
    public int getTotalPrice() {
        return getOrderPrice() * getCount();
    }
}
