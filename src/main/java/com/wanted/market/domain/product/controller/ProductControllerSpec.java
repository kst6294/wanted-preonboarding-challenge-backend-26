package com.wanted.market.domain.product.controller;

import com.wanted.market.common.dto.ResponseDto;
import com.wanted.market.domain.product.dto.ProductCreateRequest;
import com.wanted.market.domain.product.dto.ProductResponse;
import com.wanted.market.domain.product.dto.ProductUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Product", description = "상품 API")
@RequestMapping("/api/products")
public interface ProductControllerSpec {

    @Operation(summary = "상품 목록 조회", description = "판매 가능한 상품 목록을 조회합니다. (비회원 가능)")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = Page.class))
            )
    })
    @GetMapping
    ResponseEntity<ResponseDto<Page<ProductResponse>>> getProducts(
            @Parameter(description = "페이지네이션 정보")
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    );

    @Operation(summary = "상품 상세 조회", description = "상품의 상세 정보를 조회합니다. (비회원 가능)")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "상품을 찾을 수 없음"
            )
    })
    @GetMapping("/{id}")
    ResponseEntity<ResponseDto<ProductResponse>> getProduct(
            @Parameter(description = "상품 ID", required = true)
            @PathVariable Long id
    );

    @Operation(summary = "상품 등록", description = "새로운 상품을 등록합니다. (회원만 가능)")
    @SecurityRequirement(name = "JWT Authorization")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "등록 성공",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자"
            )
    })
    @PostMapping
    ResponseEntity<ResponseDto<ProductResponse>> createProduct(
            @Parameter(hidden = true)
            @AuthenticationPrincipal String email,
            @Parameter(description = "상품 정보", required = true)
            @Valid @RequestBody ProductCreateRequest request
    );

    @Operation(summary = "상품 수정", description = "상품 정보를 수정합니다. (판매자만 가능)")
    @SecurityRequirement(name = "JWT Authorization")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "수정 성공",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "권한 없음 (판매자가 아님)"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "상품을 찾을 수 없음"
            )
    })
    @PutMapping("/{id}")
    ResponseEntity<ResponseDto<ProductResponse>> updateProduct(
            @Parameter(hidden = true)
            @AuthenticationPrincipal String email,
            @Parameter(description = "상품 ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "수정할 상품 정보", required = true)
            @Valid @RequestBody ProductUpdateRequest request
    );
}
