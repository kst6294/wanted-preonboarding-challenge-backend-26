package com.market.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QCustomers is a Querydsl query type for Customers
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCustomers extends EntityPathBase<Customers> {

    private static final long serialVersionUID = 522066479L;

    public static final QCustomers customers = new QCustomers("customers");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final StringPath password = createString("password");

    public final StringPath phone = createString("phone");

    public final EnumPath<UserRole> role = createEnum("role", UserRole.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public QCustomers(String variable) {
        super(Customers.class, forVariable(variable));
    }

    public QCustomers(Path<? extends Customers> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCustomers(PathMetadata metadata) {
        super(Customers.class, metadata);
    }

}

