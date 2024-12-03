package com.wanted.market.domain.base;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
public abstract class BaseEntity extends BaseTimeEntity {
    
    @CreatedBy
    @Column(nullable = false, updatable = false, length = 100)
    private String createdBy;
    
    @LastModifiedBy
    @Column(nullable = false, length = 100)
    private String updatedBy;
}