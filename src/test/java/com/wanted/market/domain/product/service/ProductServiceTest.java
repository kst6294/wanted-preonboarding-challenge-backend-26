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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService 테스트")
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProductService productService;

    private User seller;
    private Product product;

    @BeforeEach
    void setUp() {
        // 판매자 설정
        seller = User.builder()
                .email("seller@test.com")
                .password("password")
                .name("Seller")
                .build();
        ReflectionTestUtils.setField(seller, "id", 1L);

        // 상품 설정
        product = Product.builder()
                .name("Test Product")
                .price(BigDecimal.valueOf(10000))
                .quantity(5)
                .seller(seller)
                .build();
        ReflectionTestUtils.setField(product, "id", 1L);
    }

    @Nested
    @DisplayName("상품 생성 테스트")
    class CreateProduct {

        @Test
        @DisplayName("성공: 정상적인 상품 생성")
        void createProduct_Success() {
            // Given
            ProductCreateRequest request = ProductCreateRequest.builder()
                    .name("New Product")
                    .price(BigDecimal.valueOf(15000))
                    .quantity(10)
                    .build();
            when(userRepository.findByEmail(seller.getEmail())).thenReturn(Optional.of(seller));
            when(productRepository.save(any(Product.class))).thenReturn(product);

            // When
            ProductResponse response = productService.createProduct(seller.getEmail(), request);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(product.getId());
            assertThat(response.getName()).isEqualTo(product.getName());
            assertThat(response.getPrice()).isEqualTo(product.getPrice());
            verify(productRepository).save(any(Product.class));
        }

        @Test
        @DisplayName("실패: 존재하지 않는 판매자")
        void createProduct_UserNotFound() {
            // Given
            ProductCreateRequest request = ProductCreateRequest.builder()
                    .name("New Product")
                    .price(BigDecimal.valueOf(15000))
                    .quantity(10)
                    .build();
            when(userRepository.findByEmail(any())).thenReturn(Optional.empty());

            // When & Then
            assertThrows(CustomException.class,
                    () -> productService.createProduct("unknown@test.com", request),
                    "존재하지 않는 사용자로 상품 생성 시 예외가 발생해야 함");
        }
    }

    @Nested
    @DisplayName("상품 조회 테스트")
    class GetProduct {

        @Test
        @DisplayName("성공: 단일 상품 조회")
        void getProduct_Success() {
            // Given
            when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

            // When
            ProductResponse response = productService.getProduct(product.getId());

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(product.getId());
        }

        @Test
        @DisplayName("성공: 판매 중인 상품 목록 조회")
        void getAvailableProducts_Success() {
            // Given
            PageRequest pageRequest = PageRequest.of(0, 10);
            Page<Product> productPage = new PageImpl<>(List.of(product));

            when(productRepository.findAvailableProducts(ProductStatus.ON_SALE, pageRequest))
                    .thenReturn(productPage);

            // When
            Page<ProductResponse> response = productService.getAvailableProducts(pageRequest);

            // Then
            assertThat(response.getContent()).hasSize(1);
            assertThat(response.getContent().get(0).getId()).isEqualTo(product.getId());
        }

        @Test
        @DisplayName("성공: 판매자별 상품 목록 조회")
        void getSellerProducts_Success() {
            // Given
            PageRequest pageRequest = PageRequest.of(0, 10);
            Page<Product> productPage = new PageImpl<>(List.of(product));

            when(userRepository.findById(seller.getId())).thenReturn(Optional.of(seller));
            when(productRepository.findBySeller(seller, pageRequest)).thenReturn(productPage);

            // When
            Page<ProductResponse> response = productService.getSellerProducts(
                    seller.getId(),
                    null,
                    pageRequest
            );

            // Then
            assertThat(response.getContent()).hasSize(1);
            assertThat(response.getContent().get(0).getId()).isEqualTo(product.getId());
        }
    }

    @Nested
    @DisplayName("상품 수정 테스트")
    class UpdateProduct {

        @Test
        @DisplayName("성공: 정상적인 상품 정보 수정")
        void updateProduct_Success() {
            // Given
            ProductUpdateRequest request = ProductUpdateRequest.builder()
                    .price(BigDecimal.valueOf(20000))
                    .quantity(8)
                    .build();

            when(userRepository.findByEmail(seller.getEmail())).thenReturn(Optional.of(seller));
            when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

            // When
            ProductResponse response = productService.updateProduct(
                    seller.getEmail(),
                    product.getId(),
                    request
            );

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getPrice()).isEqualTo(request.getPrice());
            assertThat(response.getQuantity()).isEqualTo(request.getQuantity());
        }

        @Test
        @DisplayName("실패: 권한 없는 사용자의 수정 시도")
        void updateProduct_UnauthorizedAccess() {
            // Given
            User unauthorizedUser = User.builder()
                    .email("unauthorized@test.com")
                    .password("password")
                    .name("Unauthorized")
                    .build();
            ReflectionTestUtils.setField(unauthorizedUser, "id", 2L);

            ProductUpdateRequest request = ProductUpdateRequest.builder()
                    .price(BigDecimal.valueOf(20000))
                    .quantity(8)
                    .build();

            when(userRepository.findByEmail(unauthorizedUser.getEmail())).thenReturn(Optional.of(unauthorizedUser));
            when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

            // When & Then
            CustomException exception = assertThrows(CustomException.class,
                    () -> productService.updateProduct(
                            unauthorizedUser.getEmail(),
                            product.getId(),
                            request
                    )
            );
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.UNAUTHORIZED_ACCESS);
        }
    }

    @Nested
    @DisplayName("재고 관리 테스트")
    class InventoryManagement {

        @Test
        @DisplayName("성공: 정상적인 재고 감소")
        void decreaseQuantity_Success() {
            // Given
            when(productRepository.findByIdWithPessimisticLock(product.getId()))
                    .thenReturn(Optional.of(product));
            int initialQuantity = product.getQuantity();

            // When
            productService.decreaseQuantity(product.getId());

            // Then
            assertThat(product.getQuantity()).isEqualTo(initialQuantity - 1);
        }

        @Test
        @DisplayName("실패: 재고 없는 상품 구매 시도")
        void decreaseQuantity_NoStock() {
            // Given
            product.updateQuantity(0);
            when(productRepository.findByIdWithPessimisticLock(product.getId()))
                    .thenReturn(Optional.of(product));

            // When & Then
            CustomException exception = assertThrows(CustomException.class,
                    () -> productService.decreaseQuantity(product.getId())
            );
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.PRODUCT_NOT_AVAILABLE);
        }
    }
}
