package test.shop.domain.model.order;

import jakarta.persistence.*;
import lombok.Getter;
import test.shop.application.dto.request.OrderItemDto;
import test.shop.domain.exception.NotEnoughStockException;
import test.shop.domain.model.item.Image;
import test.shop.domain.model.item.Item;
import test.shop.application.dto.response.ItemDto;

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

    public static OrderItem createOrderItem(Item item,  OrderItemDto dto) {
        // validate inputs
        if(item == null || dto == null) {
            throw new IllegalArgumentException("Item and dto must not be null");
        }
        if(dto.getCount() <= 0){
            throw new IllegalArgumentException("Order Count must be greater than 0");
        }

        // create order item
        OrderItem orderItem = new OrderItem();
        orderItem.saveItem(item);

        // use item's current price to ensure price integrity
        orderItem.savePrice(item.getPrice());
        orderItem.saveCount(dto.getCount());

        // remove stock
        try {
            item.removeStock(dto.getCount());
        } catch (NotEnoughStockException e) {
            throw new NotEnoughStockException("Not enough stock for item: " + item.getName());
        }

        return orderItem;
    }

    public static OrderItem createRandomOrderItem(Item item, int count) {
        OrderItem orderItem = new OrderItem();
        orderItem.saveItem(item);
        orderItem.savePrice(item.getPrice());
        orderItem.saveCount(count);
        // remove stock
        try {
            item.removeStock(count);
        } catch (NotEnoughStockException e) {
            throw new NotEnoughStockException("Not enough stock for item: " + item.getName());
        }
        return orderItem;
    }

    //==ToDTO==//
    public ItemDto toItemDto() {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(getItem().getId());
        itemDto.setName(getItem().getName());
        itemDto.setPrice(getPrice());
        itemDto.setImages(getItem().getImages().stream().map(Image::toDto).collect(Collectors.toList()));
        itemDto.setStockQuantity(getItem().getStockQuantity());
        itemDto.setCount(getCount());
        return itemDto;
    }

    public OrderItemDto toOrderItemDto() {
        return OrderItemDto.builder()
                .itemId(this.getItem().getId())
                .count(this.getCount())
                .price(this.getPrice())
                .name(this.getItem().getName())
                .thumbnailUrl(getItem().getThumbnailUrl())
                .build();
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
