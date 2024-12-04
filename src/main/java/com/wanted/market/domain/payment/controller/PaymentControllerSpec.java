package com.wanted.market.domain.payment.controller;

import com.wanted.market.common.dto.ResponseDto;
import com.wanted.market.domain.payment.dto.PaymentCreateRequest;
import com.wanted.market.domain.payment.dto.PaymentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Payment", description = "결제 API")
@RequestMapping("/api/payments")
public interface PaymentControllerSpec {

    @Operation(summary = "가상계좌 발급 요청", description = "결제를 위한 가상계좌를 발급받습니다. (회원만 가능)")
    @SecurityRequirement(name = "JWT Authorization")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "가상계좌 발급 성공",
                    content = @Content(schema = @Schema(implementation = PaymentResponse.class))
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
                    responseCode = "409",
                    description = "중복된 결제 요청"
            )
    })
    @PostMapping
    ResponseEntity<ResponseDto<PaymentResponse>> createPayment(
            @Parameter(hidden = true)
            @AuthenticationPrincipal String email,
            @Parameter(description = "결제 생성 정보", required = true)
            @Valid @RequestBody PaymentCreateRequest request
    );

    @Operation(summary = "결제 상태 조회", description = "결제의 현재 상태를 조회합니다.")
    @SecurityRequirement(name = "JWT Authorization")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = PaymentResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "결제 정보를 찾을 수 없음"
            )
    })
    @GetMapping("/{merchantUid}")
    ResponseEntity<ResponseDto<PaymentResponse>> getPayment(
            @Parameter(description = "주문번호", required = true)
            @PathVariable String merchantUid
    );

    @Operation(summary = "결제 취소", description = "결제를 취소합니다. (결제 완료 상태인 경우에만 가능)")
    @SecurityRequirement(name = "JWT Authorization")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "취소 성공",
                    content = @Content(schema = @Schema(implementation = PaymentResponse.class))
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
                    description = "결제 정보를 찾을 수 없음"
            )
    })
    @PostMapping("/{merchantUid}/cancel")
    ResponseEntity<ResponseDto<PaymentResponse>> cancelPayment(
            @Parameter(hidden = true)
            @AuthenticationPrincipal String email,
            @Parameter(description = "주문번호", required = true)
            @PathVariable String merchantUid,
            @Parameter(description = "취소 사유", required = true)
            @RequestParam @NotBlank String reason
    );

    @Operation(summary = "결제 웹훅", description = "포트원으로부터 결제 관련 웹훅을 받습니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "웹훅 처리 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "결제 정보를 찾을 수 없음"
            )
    })
    @PostMapping("/webhook")
    ResponseEntity<Void> handleWebhook(
            @Parameter(description = "상점 거래 ID", required = true)
            @RequestParam String merchant_uid,
            @Parameter(description = "포트원 거래 ID", required = true)
            @RequestParam String imp_uid
    );
}
