package test.shop.web.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import test.shop.domain.Cart;
import test.shop.domain.CartItem;
import test.shop.domain.Member;
import test.shop.domain.item.Item;
import test.shop.web.dto.ItemDto;
import test.shop.web.dto.request.CartRequestDto;
import test.shop.web.repository.CartRepository;
import test.shop.web.repository.ItemRepository;
import test.shop.web.repository.MemberRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {
    private final CartRepository cartRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    public List<ItemDto> getCartItems(Long memberId) {
        Cart cart = getCartByMemberId(memberId);
        List<CartItem> cartItems = cart.getCartItems();
        return cartItems.stream()
                .map(CartItem::toItemDto)
                .collect(Collectors.toList());
    }
    private Cart getCartByMemberId(Long memberId) {
        return cartRepository.findByMemberId(memberId)
                .orElseGet(() -> createNewCart(memberId));
    }

    private Cart createNewCart(Long memberId) {
        Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new UsernameNotFoundException("Member not found"));
        Cart cart = new Cart();
        cart.saveMember(member);
        return cartRepository.save(cart);
    }

    public void addItemToCart (Long itemId, Long memberId, int count) {
        Cart cart = getCartByMemberId(memberId);
        Item item = itemRepository.findItemById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item not found"));
        CartItem cartItem = CartItem.addItemToCart(item, count);
        cart.addCartItem(cartItem);
        cartRepository.save(cart);
    }

    public Long updateCartItem(Long itemId, Long memberId, int count) {
        Cart cart = getCartByMemberId(memberId);
        ItemDto updatedItemDto = findCartItem(cart, itemId).update(count);
        return updatedItemDto.getId();
    }

    private CartItem findCartItem(Cart cart, Long itemId) {
        return cart.getCartItems().stream()
                .filter(cartItem -> cartItem.getItem().getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("CartItem not found"));
    }

    public Long deleteCartItem(Long itemId, Long memberId) {
        Cart cart = getCartByMemberId(memberId);
        findCartItem(cart, itemId).delete();
        return itemId;
    }



}
