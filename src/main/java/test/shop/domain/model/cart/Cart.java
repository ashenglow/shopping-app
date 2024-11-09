package test.shop.domain.model.cart;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import test.shop.domain.model.member.Member;
import test.shop.application.dto.response.ItemDto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@RequiredArgsConstructor
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems = new ArrayList<>();

    public void saveMember(Member member) {
        this.member = member;
    }


    public void addCartItem(CartItem cartItem) {
        for (CartItem existingCartItem : cartItems) {
            if (existingCartItem.getItem().equals(cartItem.getItem())) {
                existingCartItem.changeCount(existingCartItem.getCount() + cartItem.getCount());
                return;
            }
        }
        cartItems.add(cartItem);
        cartItem.saveCart(this);
    }

    public List<ItemDto> getCartItemDtos() {
        return cartItems.stream().map(CartItem::toItemDto).collect(Collectors.toList());
    }


    public void deleteCartItem(CartItem cartItem) {
        cartItems.remove(cartItem);
    }
}
