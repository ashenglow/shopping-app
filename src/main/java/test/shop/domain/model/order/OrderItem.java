package test.shop.domain.model.order;

import jakarta.persistence.*;
import lombok.Getter;
import test.shop.domain.model.item.Images;
import test.shop.domain.model.item.Item;
import test.shop.application.dto.response.ItemDto;
import test.shop.application.dto.request.OrderRequestDto;

import java.util.stream.Collectors;

@Entity
@Table(name = "order_item")
@Getter
public class OrderItem {


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
    private int price; //주문 가격
    private int count; //주문 수량

    public void saveOrder(Order order) {
        this.order = order;
    }

    public void saveItem(Item item) {
        this.item = item;
    }

    public void savePrice(int price) {
        this.price = price;
    }

    public void saveCount(int count) {
        this.count = count;
    }

    //==생성 메서드=//
    public static OrderItem createOrderItem(Item item,  OrderRequestDto dto) {
        OrderItem orderItem = new OrderItem();
        orderItem.saveItem(item);
        orderItem.savePrice(dto.getPrice());
        orderItem.saveCount(dto.getCount());
        item.removeStock(dto.getCount());
        return orderItem;
    }

    //==ToDTO==//
    public ItemDto toItemDto() {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(getItem().getId());
        itemDto.setName(getItem().getName());
        itemDto.setPrice(getPrice());
        itemDto.setImages(getItem().getImages().stream().map(Images::newImageDto).collect(Collectors.toList()));
        itemDto.setStockQuantity(getItem().getStockQuantity());
        itemDto.setCount(getCount());
        return itemDto;
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
        return getPrice() * getCount();
    }
}
