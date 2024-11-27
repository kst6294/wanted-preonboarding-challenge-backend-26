package com.wanted.wanted2.payment.controller;

import com.wanted.wanted2.cancel.model.CancelDto;
import com.wanted.wanted2.cancel.model.CancelEntity;
import com.wanted.wanted2.payment.model.PaymentDto;
import com.wanted.wanted2.payment.model.PaymentEntity;
import com.wanted.wanted2.payment.service.PaymentService;
import com.wanted.wanted2.users.model.UserDetail;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
@Tag(name = "payments", description = "Payment Controller")
public class PaymentController {
    private final PaymentService paymentService;

    @GetMapping
    @Operation(summary = "사용자 기준 결제 목록", description = "사용자 기준 결제 목록 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "결제 목록 조회 성공"),
            @ApiResponse(responseCode = "404", description = "사용자 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Parameter(name = "로그인한 사용자 id", description = "조회할 사용자의 id", example = "2")
    public ResponseEntity<?> findByUser(@AuthenticationPrincipal UserDetail userDetail) {
        return paymentService.findByUser(userDetail);
    }

    @GetMapping("/{imp_uid}/balance")
    @Operation(summary = "결제 상세보기", description = "결제 정보 및 상태 확인 가능")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "결제 조회 성공"),
            @ApiResponse(responseCode = "404", description = "결제 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Parameter(name = "결제 id", description = "상세보기할 결제 id", example = "2")
    public ResponseEntity<PaymentEntity> findById(@AuthenticationPrincipal UserDetail userDetail, @PathVariable String imp_uid) {
        return paymentService.findById(userDetail, imp_uid);
    }

    @PostMapping
    @Operation(summary = "구매하기 버튼을 통해 결제 시작", description = "구매자가 구매하기 버튼 눌렀을 때 호출되는 결제 메서드")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "결제 목록 조회 성공"),
            @ApiResponse(responseCode = "404", description = "사용자 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Parameter(examples = {
            @ExampleObject(name = "examplePaymentModel", value = """ 
                        { 
                            "paymentNo" : "결제 번호",
                            "productId" : "제품 id", 
                            "sellerId" : "판매자 id", 
                            "buyerId" : "구매자 id"
                        } 
                    """)})
    public ResponseEntity<PaymentEntity> save(@AuthenticationPrincipal UserDetail userDetail, @RequestBody PaymentDto payment) {
        return paymentService.save(userDetail, payment);
    }
}
