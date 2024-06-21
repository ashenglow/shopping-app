package test.shop.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import test.shop.domain.item.Item;
import test.shop.web.dto.ItemDto;

@Entity
@Getter
@RequiredArgsConstructor
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int price;
    private int count;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    public void saveName(String name) {
        this.name = name;
    }

    public void savePrice(int price) {
        this.price = price;
    }

    public void saveCount(int count) {
        this.count = count;
    }

    public void saveItem(Item item) {
        this.item = item;
    }

    public void saveCart(Cart cart) {
        this.cart = cart;
    }

    // ==생성 메서드==//
    public static CartItem addItemToCart(Item item, int count) {
        CartItem cartItem = new CartItem();
        cartItem.saveName(item.getName());
        cartItem.savePrice(item.getPrice());
        cartItem.saveCount(count);
        cartItem.saveItem(item);
        return cartItem;
    }

    // ==조회 메서드==//
    public int getTotalPrice() {
        return getPrice() * getCount();
    }

    // ==ToDTO==//
    public ItemDto toItemDto() {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(getItem().getId());
        itemDto.setName(getName());
        itemDto.setPrice(getPrice());
        itemDto.setCount(getCount());
        itemDto.setStockQuantity(getItem().getStockQuantity());
        itemDto.setImages(getItem().getImages().stream().map(Images::newImageDto).toList());
        return itemDto;
    }

    //==비즈니스 메서드    ==//

    public void changeCount(int count) {
        this.count = count;
    }

    public ItemDto update(int count) {
        saveCount(count);
        return this.toItemDto();
    }


    public void delete() {
        this.cart.deleteCartItem(this);
    }
}
