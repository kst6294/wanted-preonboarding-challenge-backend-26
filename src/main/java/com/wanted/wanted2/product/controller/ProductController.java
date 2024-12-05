package com.wanted.wanted2.product.controller;

import com.wanted.wanted2.product.model.ProductDto;
import com.wanted.wanted2.product.model.ProductEntity;
import com.wanted.wanted2.product.service.ProductService;
import com.wanted.wanted2.users.model.UserDetail;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    @GetMapping
    @Operation(summary = "등록된 목록", description = "등록되어있는 전체 제품 목록")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "제품 목록 조회 성공"),
            @ApiResponse(responseCode = "404", description = "제품 목록 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<List<ProductEntity>> findAll() {
        return productService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "제품 상세보기", description = "제품 정보 및 상태 확인 가능")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "제품 조회 성공"),
            @ApiResponse(responseCode = "404", description = "제품 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Parameter(name = "제품 id", description = "상세보기할 제품 id", example = "2")
    public ResponseEntity<ProductEntity> findById(@PathVariable Long id) {
        return productService.findById(id);
    }

    @GetMapping("/findByUser")
    @Operation(summary = "내가 등록한 제품 목록", description = "본인이 등록한 모든 제품 목록 확인 가능")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "제품 목록 조회 성공"),
            @ApiResponse(responseCode = "404", description = "제품 목록 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Parameter(name = "회원 id", description = "제품에 등록된 판매자 id", example = "2")
    public ResponseEntity<List<ProductEntity>> findByUser(@AuthenticationPrincipal UserDetail userDetail, @RequestParam Long id) {
        return productService.findByUser(userDetail, id);
    }

    @PostMapping
    @Operation(summary = "제품 등록", description = "회원이 제품 등록할 때 호출될 메서드")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "제품 등록 성공"),
            @ApiResponse(responseCode = "404", description = "찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Parameter(
            name = "product",
            description = "등록할 제품 정보",
            required = true,
            examples = @ExampleObject(
                    name = "exampleProductDto",
                    value = """
                            {
                                "name": "제품 이름",
                                "price": "제품 가격",
                                "seller": "판매자 id"
                            }
                            """
            )
    )
    public ResponseEntity<ProductEntity> save(@AuthenticationPrincipal UserDetail userDetail, @RequestBody ProductDto product) {
        return productService.save(userDetail, product);
    }

    @PatchMapping
    @Operation(summary = "제품 상태 수정", description = "등록된 제품의 상태를 완료로 변경할 때 호출하는 메서드")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "주문 목록 조회 성공"),
            @ApiResponse(responseCode = "404", description = "사용자 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Parameter(
            name = "product",
            description = "수정할 제품 정보",
            required = true,
            examples = @ExampleObject(
                    name = "exampleProductDto",
                    value = """
                            {
                                "id": "제품 id",
                                "seller": "판매자 id"
                            }
                            """
            )
    )
    public ResponseEntity<ProductEntity> update(@AuthenticationPrincipal UserDetail userDetail, @RequestBody ProductDto product) {
        return productService.update(userDetail, product);
    }
}
