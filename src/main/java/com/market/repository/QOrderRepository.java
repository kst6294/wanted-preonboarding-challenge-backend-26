package com.market.repository;

import com.market.domain.dto.SimpleOrderResponse;

public interface QOrderRepository {

    SimpleOrderResponse findByMerchantUid(String merchantUid);
}
