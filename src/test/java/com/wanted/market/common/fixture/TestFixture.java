package com.wanted.market.common.fixture;

import com.wanted.market.domain.payment.Payment;
import com.wanted.market.domain.payment.PaymentStatus;
import com.wanted.market.domain.product.Product;
import com.wanted.market.domain.transaction.Transaction;
import com.wanted.market.domain.user.User;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TestFixture {

    private TestFixture() {
    }

    private static Long sequence = 1L;

    public static User createUser(String email) {
        User user = User.builder()
                .email(email)
                .password("testPassword123!@")
                .name("테스트유저")
                .build();
        ReflectionTestUtils.setField(user, "id", nextId());
        return user;
    }

    public static Product createProduct(int quantity) {
        User seller = createUser("seller@test.com");
        return createProduct(seller, quantity);
    }

    public static Product createProduct(User seller, int quantity) {
        Product product = Product.builder()
                .name("테스트상품")
                .price(BigDecimal.valueOf(10000))
                .seller(seller)
                .quantity(quantity)
                .build();
        ReflectionTestUtils.setField(product, "id", nextId());
        return product;
    }

    public static Transaction createTransaction() {
        Product product = createProduct(1);
        User buyer = createUser("buyer@test.com");
        Transaction transaction = Transaction.builder()
                .product(product)
                .buyer(buyer)
                .seller(product.getSeller())
                .purchasePrice(BigDecimal.valueOf(10000))
                .build();
        ReflectionTestUtils.setField(transaction, "id", nextId());
        return transaction;
    }

    public static Payment createPayment(Transaction transaction) {
        Payment payment = Payment.builder()
                .transaction(transaction)
                .merchantUid("test_merchant_" + nextId())
                .amount(BigDecimal.valueOf(10000))
                .build();
        ReflectionTestUtils.setField(payment, "id", nextId());
        return payment;
    }

    private static synchronized Long nextId() {
        return sequence++;
    }

    public static class PaymentBuilder {
        private Transaction transaction;
        private String merchantUid;
        private BigDecimal amount;
        private PaymentStatus status = PaymentStatus.READY;
        private String impUid;
        private String virtualAccount;
        private String virtualBankCode;
        private String virtualBankName;
        private String virtualAccountHolder;
        private LocalDateTime virtualDueDate;

        public PaymentBuilder(Transaction transaction) {
            this.transaction = transaction;
            this.merchantUid = "test_merchant_" + TestFixture.nextId();
            this.amount = transaction.getPurchasePrice();
        }

        public PaymentBuilder withVirtualAccount() {
            this.virtualAccount = "0123456789";
            this.virtualBankCode = "088";
            this.virtualBankName = "신한은행";
            this.virtualAccountHolder = "원티드마켓";
            this.virtualDueDate = LocalDateTime.now().plusDays(7);
            this.status = PaymentStatus.PENDING;
            return this;
        }

        public PaymentBuilder withConfirmedPayment() {
            withVirtualAccount();
            this.impUid = "imp_" + TestFixture.nextId();
            this.status = PaymentStatus.PAID;
            return this;
        }

        public PaymentBuilder withFailedPayment() {
            withVirtualAccount();
            this.status = PaymentStatus.FAILED;
            return this;
        }

        public PaymentBuilder withCancelledPayment() {
            withConfirmedPayment();
            this.status = PaymentStatus.CANCELLED;
            return this;
        }

        public Payment build() {
            Payment payment = Payment.builder()
                    .transaction(transaction)
                    .merchantUid(merchantUid)
                    .amount(amount)
                    .build();

            ReflectionTestUtils.setField(payment, "id", TestFixture.nextId());

            if (status != PaymentStatus.READY) {
                ReflectionTestUtils.setField(payment, "status", status);
                ReflectionTestUtils.setField(payment, "virtualAccount", virtualAccount);
                ReflectionTestUtils.setField(payment, "virtualBankCode", virtualBankCode);
                ReflectionTestUtils.setField(payment, "virtualBankName", virtualBankName);
                ReflectionTestUtils.setField(payment, "virtualAccountHolder", virtualAccountHolder);
                ReflectionTestUtils.setField(payment, "virtualDueDate", virtualDueDate);

                if (impUid != null) {
                    ReflectionTestUtils.setField(payment, "impUid", impUid);
                }
            }

            return payment;
        }
    }

    public static PaymentBuilder payment(Transaction transaction) {
        return new PaymentBuilder(transaction);
    }
}
