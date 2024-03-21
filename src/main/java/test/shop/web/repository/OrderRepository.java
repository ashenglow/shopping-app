package test.shop.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import test.shop.domain.Order;
import test.shop.domain.OrderSearchCond;

import java.util.List;


public interface OrderRepository extends JpaRepository<Order, Long>{
    public Order findOrderById(Long orderId);

}
