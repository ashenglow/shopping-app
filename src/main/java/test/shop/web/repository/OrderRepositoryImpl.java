package test.shop.web.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import test.shop.domain.*;
import test.shop.web.form.OrderForm;
import test.shop.web.form.QOrderForm;

import java.util.List;

import static org.springframework.util.StringUtils.hasLength;
import static test.shop.domain.QMember.member;
import static test.shop.domain.QOrder.order;

public class OrderRepositoryImpl implements OrderRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public OrderRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    //회원명, 상품명, 주문상품
    @Override
    public List<OrderForm> search(OrderSearchCond condition) {
        BooleanExpression memberNameCond = memberNameEq(condition.getMemberName());
        BooleanExpression orderStatusCond = orderStatusEq(condition.getOrderStatus());

        return queryFactory
                .select(new QOrderForm(
                        order.id,
                        member.id,
                        member.name,
                        order.status,
                        order.orderItems
                ))
                .from(order)
                .leftJoin(order.member, member)
                .where(memberNameCond, orderStatusCond)
                .fetch();
    }

    private BooleanExpression memberNameEq(String memberName) {
    return !hasLength(memberName) ? null : member.name.eq(memberName);
}
    private BooleanExpression orderStatusEq(OrderStatus orderStatus) {
        return orderStatus == null ? null : order.status.eq(orderStatus);

}



}
