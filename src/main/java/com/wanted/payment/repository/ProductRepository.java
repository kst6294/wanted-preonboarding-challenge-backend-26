package com.wanted.payment.repository;

import com.wanted.payment.schema.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findAllById(List<Integer> ids);
}
