package com.wanted.payment.repository;

import com.wanted.payment.schema.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, Integer> {
}
