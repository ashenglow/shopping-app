package test.shop.web.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import test.shop.domain.*;

import java.util.List;

import static org.springframework.util.StringUtils.hasLength;
import static test.shop.domain.QMember.member;
import static test.shop.domain.QOrder.order;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

    private final JPAQueryFactory queryFactory;

    //회원명, 상품명, 주문상품

    public List<Order> search(OrderSearchCond condition, int offset, int limit) {
        BooleanExpression memberNameCond = memberNameEq(condition.getMemberName());
        BooleanExpression orderStatusCond = orderStatusEq(condition.getOrderStatus());

        return queryFactory
                .select(order)
                .from(order)
                .join(order.member, member).fetchJoin()
                .where(memberNameCond, orderStatusCond)
                .offset(offset)
                .limit(limit)
                .fetch();


    }

    public List<Order> findOrdersByMemberId(Long memberId, int offset, int limit) {
        return queryFactory
                .selectFrom(order)
                .join(order.member, member).fetchJoin()
                .where(member.id.eq(memberId))
                .offset(offset)
                .limit(limit)
                .fetch();
    }

    private BooleanExpression memberNameEq(String memberName) {
    return !hasLength(memberName) ? null : member.username.contains(memberName);
}
    private BooleanExpression orderStatusEq(OrderStatus orderStatus) {
        return orderStatus == null ? null : order.status.eq(orderStatus);

}



}
