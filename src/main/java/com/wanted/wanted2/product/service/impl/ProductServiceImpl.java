package com.wanted.wanted2.product.service.impl;

import com.wanted.wanted2.product.model.ProductDto;
import com.wanted.wanted2.product.model.ProductEntity;
import com.wanted.wanted2.product.model.Status;
import com.wanted.wanted2.product.repository.ProductRepository;
import com.wanted.wanted2.product.service.ProductService;
import com.wanted.wanted2.users.model.UserDetail;
import com.wanted.wanted2.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    public ResponseEntity<List<ProductEntity>> findAll() {
        return ResponseEntity.ok(productRepository.findAll());
    }

    @Override
    public ResponseEntity<ProductEntity> findById(Long id) {
        return productRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<List<ProductEntity>> findByUser(UserDetail userDetail, Long id) {
        return Optional.ofNullable(userDetail)
                .filter(detail -> !detail.getAuthorities().isEmpty())
                .map(detail -> ResponseEntity.ok(productRepository.findBySeller(id)))
                .orElse(ResponseEntity.status(HttpStatus.FORBIDDEN).body(null));
    }

    @Override
    public ResponseEntity<ProductEntity> save(UserDetail userDetail, ProductDto product) {
        return Optional.ofNullable(userDetail)
                .filter(detail -> !detail.getAuthorities().isEmpty())
                .flatMap(user -> userRepository.findById(product.getSeller()))
                .map(user -> {
                    ProductEntity productEntity = ProductEntity.builder()
                            .name(product.getName())
                            .price(product.getPrice())
                            .seller(user)
                            .build();
                    ProductEntity savedProduct = productRepository.save(productEntity);
                    return ResponseEntity.ok(savedProduct);
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Override
    public ResponseEntity<ProductEntity> update(UserDetail userDetail, ProductDto product) {
        if (userDetail == null || userDetail.getAuthorities().isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return userRepository.findById(product.getSeller())
                .filter(user -> user.equals(userDetail.getUser()))
                .flatMap(user -> productRepository.findById(product.getId()))
                .map(existingProduct -> {
                    existingProduct.setStatus(Status.COMPLETED);
                    ProductEntity updatedProduct = productRepository.save(existingProduct);
                    return ResponseEntity.ok(updatedProduct);
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}
