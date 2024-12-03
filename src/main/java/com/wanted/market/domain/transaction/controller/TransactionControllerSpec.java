package com.wanted.market.domain.transaction.controller;

import com.wanted.market.common.dto.ResponseDto;
import com.wanted.market.domain.transaction.dto.TransactionCreateRequest;
import com.wanted.market.domain.transaction.dto.TransactionResponse;
import com.wanted.market.domain.transaction.dto.TransactionStatusUpdateRequest;
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

@Tag(name = "Transaction", description = "거래 API")
@RequestMapping("/api/transactions")
@SecurityRequirement(name = "JWT Authorization")
public interface TransactionControllerSpec {

    @Operation(summary = "거래 생성", description = "상품 구매를 위한 거래를 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "거래 생성 성공",
                    content = @Content(schema = @Schema(implementation = TransactionResponse.class))
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
                    responseCode = "404",
                    description = "상품을 찾을 수 없음"
            )
    })
    @PostMapping
    ResponseEntity<ResponseDto<TransactionResponse>> createTransaction(
            @Parameter(hidden = true)
            @AuthenticationPrincipal String email,
            @Parameter(description = "거래 생성 정보", required = true)
            @Valid @RequestBody TransactionCreateRequest request
    );

    @Operation(summary = "거래 상태 변경", description = "거래의 상태를 변경합니다. (승인/확정)")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "상태 변경 성공",
                    content = @Content(schema = @Schema(implementation = TransactionResponse.class))
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
                    description = "권한 없음"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "거래를 찾을 수 없음"
            )
    })
    @PutMapping("/{id}/status")
    ResponseEntity<ResponseDto<TransactionResponse>> updateTransactionStatus(
            @Parameter(hidden = true)
            @AuthenticationPrincipal String email,
            @Parameter(description = "거래 ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "변경할 거래 상태 정보", required = true)
            @Valid @RequestBody TransactionStatusUpdateRequest request
    );

    @Operation(summary = "구매 내역 조회", description = "내가 구매한 거래 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = Page.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자"
            )
    })
    @GetMapping("/my/purchases")
    ResponseEntity<ResponseDto<Page<TransactionResponse>>> getPurchasedTransactions(
            @Parameter(hidden = true)
            @AuthenticationPrincipal String email,
            @Parameter(description = "페이지네이션 정보")
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    );

    @Operation(summary = "진행중인 거래 조회", description = "내가 참여중인 진행중인 거래 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = Page.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자"
            )
    })
    @GetMapping("/my/ongoing")
    ResponseEntity<ResponseDto<Page<TransactionResponse>>> getOngoingTransactions(
            @Parameter(hidden = true)
            @AuthenticationPrincipal String email,
            @Parameter(description = "페이지네이션 정보")
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    );
}
