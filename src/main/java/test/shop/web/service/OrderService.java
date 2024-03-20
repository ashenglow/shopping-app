package test.shop.web.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import test.shop.domain.*;
import test.shop.domain.item.Item;
import test.shop.domain.OrderSearchCond;
import test.shop.web.form.OrderForm;
import test.shop.web.repository.ItemRepository;
import test.shop.web.repository.MemberRepository;
import test.shop.web.repository.OrderRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    /**
     * 주문
     */
    @Transactional
    public Long order(Long memberId, Long itemId, int count) {

        //엔티티 조회
        Member member = memberRepository.findMemberById(memberId);
        Item item = itemRepository.findItemById(itemId);
        //배송정보 생성
        Delivery delivery = new Delivery();
        delivery.saveAddress(member.getAddress());
        delivery.saveStatus(DeliveryStatus.READY);
        //주문상품 생성
        //한 번에 한 상품만 가능
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);
        //주문 생성
        Order order = Order.createOrder(member, delivery, orderItem);

        //주문 저장
        orderRepository.save(order);
        return order.getId();
    }

    /**
     * 주문 취소
     */
    @Transactional
    public void cancelOrder(Long orderId) {
        //주문 엔티티 조회
        Order order = orderRepository.findOrderById(orderId);
        //주문 취소
        order.cancel();
    }

    /**
     * 주문 검색
     */
    public List<OrderForm> findOrders(OrderSearchCond orderSearchCond) {
        return orderRepository.search(orderSearchCond);
    }
}
