package test.shop.web.repository;

import test.shop.domain.OrderSearchCond;
import test.shop.web.form.OrderForm;

import java.util.List;

public interface OrderRepositoryCustom {
    List<OrderForm> search(OrderSearchCond orderSearchCond);
}
