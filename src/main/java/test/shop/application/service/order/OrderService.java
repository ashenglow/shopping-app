package test.shop.application.service.order;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import test.shop.application.dto.request.OrderItemDto;
import test.shop.domain.exception.InvalidAddressException;
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
import test.shop.domain.value.Address;
import test.shop.infrastructure.monitoring.aspect.QueryPerformanceMonitor;
import test.shop.infrastructure.persistence.jpa.query.OrderQueryRepository;
import test.shop.application.dto.response.OrderDto;
import test.shop.application.dto.request.OrderRequestDto;
import test.shop.infrastructure.persistence.jpa.repository.SpecificationBuilder;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final QueryPerformanceMonitor monitor;

    /**
     * 주문
     */
    @Transactional
    public Long order(Long memberId, OrderRequestDto orderRequest) {
        long startTime = System.currentTimeMillis();
        monitor.setCurrentStep("memberValidation");

        try {

            // Member validation
            Member member = memberRepository.findMemberById(memberId)
                    .orElseThrow(() -> new UsernameNotFoundException("Member not found"));

            //validate address before proceeding with order

            validateAddress(member);

            // Delivery creation
            monitor.setCurrentStep("deliveryCreation");
            Delivery delivery = createDelivery(member);

            // Order items
            monitor.setCurrentStep("itemsProcessing");
            List<OrderItem> orderItems = createOrderItems(orderRequest.getOrderItems());

            // Order creation and save
            monitor.setCurrentStep("orderSave");
            Order order = Order.createOrder(member, delivery, orderItems);
            Order savedOrder = orderRepository.save(order);

            long totalTime = System.currentTimeMillis() - startTime;
            log.info("Order process completed in {}ms for member: {}", totalTime, memberId);

            return savedOrder.getId();
        } finally {
            monitor.clearCurrentStep();
        }
    }
    private void logOrderFlowTimings(Map<String, Long> stepTimings, long totalTime) {
        log.info("Order Flow Execution Times:");
        log.info("Total Execution Time: {}ms", totalTime);
        stepTimings.forEach((step, time) ->
                log.info("  {} : {}ms", step, time));
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
     * 주소 유무 확인
     */
    private void validateAddress(Member member){
        Address address = member.getAddress();
        if (address == null ||
                !StringUtils.hasText(address.getZipcode()) ||
                !StringUtils.hasText(address.getBaseAddress()))  {
            throw new InvalidAddressException("Shipping address is required for checkout");
        }
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
    private List<OrderItem> createOrderItems(List<OrderItemDto> itemDtos) {
       return itemDtos.stream()
               .map(this::processOrderItem)
               .collect(Collectors.toList());
    }

    private OrderItem processOrderItem(OrderItemDto dto) {
        Item item = getItem(dto.getItemId());
        return OrderItem.createOrderItem(item, dto);
    }

    private Item getItem(Long itemId) {
        Item item = itemRepository.findItemById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("item doesn't exist"));
        return item;
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
