package com.market.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QOrderProducts is a Querydsl query type for OrderProducts
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOrderProducts extends EntityPathBase<OrderProducts> {

    private static final long serialVersionUID = -2135707732L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QOrderProducts orderProducts = new QOrderProducts("orderProducts");

    public final NumberPath<java.math.BigInteger> amount = createNumber("amount", java.math.BigInteger.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QOrders orderId;

    public final QProducts productId;

    public final EnumPath<ReservationStatus> reservationStatus = createEnum("reservationStatus", ReservationStatus.class);

    public QOrderProducts(String variable) {
        this(OrderProducts.class, forVariable(variable), INITS);
    }

    public QOrderProducts(Path<? extends OrderProducts> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QOrderProducts(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QOrderProducts(PathMetadata metadata, PathInits inits) {
        this(OrderProducts.class, metadata, inits);
    }

    public QOrderProducts(Class<? extends OrderProducts> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.orderId = inits.isInitialized("orderId") ? new QOrders(forProperty("orderId"), inits.get("orderId")) : null;
        this.productId = inits.isInitialized("productId") ? new QProducts(forProperty("productId")) : null;
    }

}

