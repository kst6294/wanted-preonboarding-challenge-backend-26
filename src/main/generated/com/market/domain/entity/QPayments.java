package com.market.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPayments is a Querydsl query type for Payments
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPayments extends EntityPathBase<Payments> {

    private static final long serialVersionUID = -1839051245L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPayments payments = new QPayments("payments");

    public final NumberPath<java.math.BigInteger> amount = createNumber("amount", java.math.BigInteger.class);

    public final NumberPath<Long> buyerId = createNumber("buyerId", Long.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath impUid = createString("impUid");

    public final StringPath merchantUid = createString("merchantUid");

    public final QOrders orders;

    public final DateTimePath<java.time.LocalDateTime> paidAt = createDateTime("paidAt", java.time.LocalDateTime.class);

    public final StringPath payMethod = createString("payMethod");

    public final StringPath status = createString("status");

    public QPayments(String variable) {
        this(Payments.class, forVariable(variable), INITS);
    }

    public QPayments(Path<? extends Payments> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPayments(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPayments(PathMetadata metadata, PathInits inits) {
        this(Payments.class, metadata, inits);
    }

    public QPayments(Class<? extends Payments> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.orders = inits.isInitialized("orders") ? new QOrders(forProperty("orders"), inits.get("orders")) : null;
    }

}

