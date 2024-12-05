package com.market.repository;

import com.market.domain.entity.Payments;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.market.domain.entity.QPayments.payments;

@Slf4j
@RequiredArgsConstructor
public class QPaymentRepositoryImpl implements QPaymentRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Payments findByImpUid(String impUid) {
        return jpaQueryFactory
                .selectFrom(payments)
                .where(payments.impUid.eq(impUid))
                .fetchOne();
    }

    @Override
    public Payments findByMerchantUid(String merchantUid) {
        return jpaQueryFactory
                .selectFrom(payments)
                .where(payments.merchantUid.eq(merchantUid))
                .fetchOne();
    }
}
