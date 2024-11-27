package com.wanted.wanted2.cancel.controller;

import com.wanted.wanted2.cancel.model.CancelDto;
import com.wanted.wanted2.cancel.model.CancelEntity;
import com.wanted.wanted2.cancel.service.CancelService;
import com.wanted.wanted2.payment.model.PaymentDto;
import com.wanted.wanted2.payment.model.PaymentEntity;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cancel")
@Tag(name = "cancel", description = "Cancel Controller")
public class CancelController {
    private final CancelService cancelService;

    @PostMapping
    @Operation(summary = "결제 취소", description = "결제 완료되었을 때 오더 페이지에서 호출되는 취소 메서드")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "취소 성공"),
            @ApiResponse(responseCode = "404", description = "결제 정보 찾을 수 없음"),
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
    public ResponseEntity<CancelEntity> cancel(@AuthenticationPrincipal UserDetail userDetail, @RequestBody CancelDto cancel) { return cancelService.save(userDetail, cancel); }
}
