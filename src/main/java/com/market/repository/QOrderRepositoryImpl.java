package com.market.repository;

import com.market.domain.dto.QSimpleOrderResponse;
import com.market.domain.dto.SimpleOrderResponse;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.market.domain.entity.QOrders.orders;

@Slf4j
@RequiredArgsConstructor
public class QOrderRepositoryImpl implements QOrderRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public SimpleOrderResponse findByMerchantUid(String merchantUid) {
        return jpaQueryFactory
                .select(new QSimpleOrderResponse(
                        orders.id,
                        orders.merchantUid,
                        orders.orderedAt,
                        orders.seller.name,
                        orders.buyer.name,
                        orders.orderProducts.productId.name,
                        orders.orderProducts.productId.quantity,
                        orders.orderProducts.amount,
                        orders.orderProducts.reservationStatus
                ))
                .from(orders)
                .where(orders.merchantUid.eq(merchantUid))
                .fetchOne();
    }
}
