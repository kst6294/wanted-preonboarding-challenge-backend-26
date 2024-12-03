package com.wanted.market.domain.product.service;

import com.wanted.market.common.exception.CustomException;
import com.wanted.market.common.exception.ErrorCode;
import com.wanted.market.domain.product.Product;
import com.wanted.market.domain.product.ProductStatus;
import com.wanted.market.domain.product.dto.ProductCreateRequest;
import com.wanted.market.domain.product.dto.ProductResponse;
import com.wanted.market.domain.product.dto.ProductUpdateRequest;
import com.wanted.market.domain.product.repository.ProductRepository;
import com.wanted.market.domain.user.User;
import com.wanted.market.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    /**
     * 상품을 생성합니다.
     */
    @Transactional
    public ProductResponse createProduct(String email, ProductCreateRequest request) {
        User seller = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Product product = Product.builder()
                .name(request.getName())
                .price(request.getPrice())
                .quantity(request.getQuantity())
                .seller(seller)
                .build();

        return ProductResponse.from(productRepository.save(product));
    }

    /**
     * 상품 정보를 조회합니다.
     */
    public ProductResponse getProduct(Long productId) {
        return ProductResponse.from(findProductById(productId));
    }

    /**
     * 판매 중인 상품 목록을 조회합니다.
     */
    public Page<ProductResponse> getAvailableProducts(Pageable pageable) {
        return productRepository.findAvailableProducts(ProductStatus.ON_SALE, pageable)
                .map(ProductResponse::from);
    }

    /**
     * 판매자의 상품 목록을 조회합니다.
     */
    public Page<ProductResponse> getSellerProducts(Long sellerId, ProductStatus status, Pageable pageable) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Page<Product> products = status == null ?
                productRepository.findBySeller(seller, pageable) :
                productRepository.findBySellerAndStatus(seller, status, pageable);

        return products.map(ProductResponse::from);
    }

    /**
     * 상품을 검색합니다.
     */
    public Page<ProductResponse> searchProducts(String keyword, Pageable pageable) {
        return productRepository.findByNameContaining(keyword, pageable)
                .map(ProductResponse::from);
    }

    /**
     * 상품 정보를 수정합니다.
     */
    @Transactional
    public ProductResponse updateProduct(String email, Long id, ProductUpdateRequest request) {
        User seller = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        if (!product.getSeller().equals(seller)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        product.updatePrice(request.getPrice());
        product.updateQuantity(request.getQuantity());
        return ProductResponse.from(product);
    }

    /**
     * 상품 구매를 위한 재고 확인 및 감소 처리를 합니다.
     */
    @Transactional
    public void decreaseQuantity(Long productId) {
        Product product = productRepository.findByIdWithPessimisticLock(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        if (!product.canPurchase()) {
            throw new CustomException(ErrorCode.PRODUCT_NOT_AVAILABLE);
        }

        product.decreaseQuantity();
    }

    private Product findProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    private Product findProductByIdAndSeller(Long productId, Long sellerId) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return productRepository.findByIdAndSeller(productId, seller)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
    }
}
