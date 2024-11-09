package test.shop.application.service.order;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import test.shop.domain.model.delivery.Delivery;
import test.shop.domain.model.delivery.DeliveryStatus;
import test.shop.domain.model.item.Item;
import test.shop.domain.model.order.OrderSearchCond;
import test.shop.domain.model.member.Member;
import test.shop.domain.model.order.Order;
import test.shop.domain.model.order.OrderItem;
import test.shop.domain.repository.ItemRepository;
import test.shop.domain.repository.MemberRepository;
import test.shop.domain.repository.OrderRepository;
import test.shop.infrastructure.persistence.jpa.query.OrderQueryRepository;
import test.shop.application.dto.response.OrderDto;
import test.shop.application.dto.request.OrderRequestDto;
import test.shop.infrastructure.persistence.jpa.repository.SpecificationBuilder;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    /**
     * 주문
     */
    @Transactional
    public Long order(Long memberId, List<OrderRequestDto> dtos) {

        //엔티티 조회
        Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new UsernameNotFoundException("Member not found"));
        //배송정보 생성
        Delivery delivery = createDelivery(member);
        //주문상품 생성
        List<OrderItem> orderItems = createOrderItems(dtos);
        //주문 생성
        Order order = Order.createOrder(member, delivery, orderItems);
        //주문 저장
        orderRepository.save(order);
        return order.getId();
    }

    public OrderDto findOrderById(Long orderId) {
        Order order = orderRepository.findOrderById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("order doesn't exist"));
        return order.toOrderDto();
    }

    public Page<OrderDto> findOrdersByMemberId(Long memberId, int offset, int limit) {
        Map<String, Object> params = new HashMap<>();
        params.put("member.id", memberId);
        Pageable pageable = PageRequest.of(offset, limit);
        Specification<Order> spec = new SpecificationBuilder<Order>().buildSpecification(params);
        if (spec == null) {
            return orderRepository.findAll(pageable).map(Order::toOrderDto);
        }
        return orderRepository.findAll(spec, pageable).map(Order::toOrderDto);

    }
    /**
     * 배송정보 생성
     */
    private Delivery createDelivery(Member member) {
        Delivery delivery = new Delivery();
        delivery.saveAddress(member.getAddress());
        delivery.saveStatus(DeliveryStatus.READY);
        return delivery;
    }

    /**
     * 주문상품 생성
     */
    private List<OrderItem> createOrderItems(List<OrderRequestDto> dtos) {
        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderRequestDto dto : dtos) {
            Item item = itemRepository.findItemById(dto.getItemId())
                    .orElseThrow(() -> new EntityNotFoundException("item doesn't exist"));
            orderItems.add(OrderItem.createOrderItem(item, dto));
        }
        return orderItems;
    }

    /**
     * 주문 취소
     */
    @Transactional
    public void cancelOrder(Long orderId) {
        //주문 엔티티 조회
        Order order = orderRepository.findOrderById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("order doesn't exist"));
        //주문 취소
        order.cancel();
    }

    /**
     * 주문 검색
     */
    public List<Order> findOrders(OrderSearchCond orderSearchCond, int offset, int limit) {
        return orderQueryRepository.search(orderSearchCond, offset, limit);
    }

    public List<OrderDto> getMemberOrders(Long memberId, int offset, int limit) {
        List<Order> orders = orderQueryRepository.findOrdersByMemberId(memberId, offset, limit);
        return orders.stream()
                .map(Order::toOrderDto)
                .collect(Collectors.toList());
    }
}
