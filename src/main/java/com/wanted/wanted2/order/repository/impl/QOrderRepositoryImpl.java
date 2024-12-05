package com.wanted.wanted2.order.repository.impl;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wanted.wanted2.order.model.OrderEntity;
import com.wanted.wanted2.order.repository.QOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class QOrderRepositoryImpl implements QOrderRepository {
    private final JPAQueryFactory queryFactory;

    private final QOrderEntity qOrder = QOrderEntity.orderEntity;
    private final QProductEntity qProduct = QProductEntity.productEntity;
    private final QPaymentEntity qPayment = QPaymentEntity.paymentEntity;
    private final QUserEntity qSeller = new QUserEntity("seller");
    private final QUserEntity qBuyer = new QUserEntity("buyer");

    @Override
    public List<OrderEntity> findByUser(Long userId) {
        return queryFactory
                .selectFrom(qOrder)
                .leftJoin(qOrder.product, qProduct)
                .leftJoin(qOrder.payment, qPayment)
                .leftJoin(qProduct.seller, qSeller)
                .leftJoin(qPayment.buyer, qBuyer)
                .where(qSeller.id.eq(userId).or(qBuyer.id.eq(userId)))
                .fetch();
    }
}
