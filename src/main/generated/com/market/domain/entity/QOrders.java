package com.market.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QOrders is a Querydsl query type for Orders
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOrders extends EntityPathBase<Orders> {

    private static final long serialVersionUID = 310780747L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QOrders orders = new QOrders("orders");

    public final QCustomers buyer;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath merchantUid = createString("merchantUid");

    public final DateTimePath<java.time.LocalDateTime> orderedAt = createDateTime("orderedAt", java.time.LocalDateTime.class);

    public final QOrderProducts orderProducts;

    public final QPayments payments;

    public final QCustomers seller;

    public QOrders(String variable) {
        this(Orders.class, forVariable(variable), INITS);
    }

    public QOrders(Path<? extends Orders> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QOrders(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QOrders(PathMetadata metadata, PathInits inits) {
        this(Orders.class, metadata, inits);
    }

    public QOrders(Class<? extends Orders> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.buyer = inits.isInitialized("buyer") ? new QCustomers(forProperty("buyer")) : null;
        this.orderProducts = inits.isInitialized("orderProducts") ? new QOrderProducts(forProperty("orderProducts"), inits.get("orderProducts")) : null;
        this.payments = inits.isInitialized("payments") ? new QPayments(forProperty("payments"), inits.get("payments")) : null;
        this.seller = inits.isInitialized("seller") ? new QCustomers(forProperty("seller")) : null;
    }

}

