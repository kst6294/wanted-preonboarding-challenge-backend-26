package com.market.repository;

import com.market.response.SimpleOrderResponse;

public interface QOrderRepository {

    SimpleOrderResponse findByMerchantUid(String merchantUid);
}
