package com.wanted.market.domain.transaction.dto;

import com.wanted.market.domain.product.Product;
import com.wanted.market.domain.transaction.Transaction;
import com.wanted.market.domain.transaction.TransactionStatus;
import com.wanted.market.domain.user.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
        Long id,
        Long productId,
        String productName,
        Long buyerId,
        String buyerName,
        Long sellerId,
        String sellerName,
        TransactionStatus status,
        BigDecimal purchasePrice,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static TransactionResponse from(Transaction transaction) {
        Product product = transaction.getProduct();
        User buyer = transaction.getBuyer();
        User seller = transaction.getSeller();

        return new TransactionResponse(
                transaction.getId(),
                product.getId(),
                product.getName(),
                buyer.getId(),
                buyer.getName(),
                seller.getId(),
                seller.getName(),
                transaction.getStatus(),
                transaction.getPurchasePrice(),
                transaction.getCreatedAt(),
                transaction.getUpdatedAt()
        );
    }
}
