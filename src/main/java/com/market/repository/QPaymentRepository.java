package com.market.repository;

import com.market.domain.entity.Payments;

public interface QPaymentRepository {

    Payments findByImpUid(String impUid);

    Payments findByMerchantUid(String merchantUid);
}
