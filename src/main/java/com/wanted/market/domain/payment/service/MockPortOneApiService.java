package com.wanted.market.domain.payment.service;

import com.wanted.market.domain.payment.dto.VirtualAccountInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class MockPortOneApiService {

    // 실제 PG사 연동 없이 가상계좌 발급 시뮬레이션
    public VirtualAccountInfo requestVirtualAccount(String merchantUid, Long amount) {
        // 실제 발급 대신 테스트용 가상계좌 정보 반환
        log.info("Requesting virtual account for order: {}, amount: {}", merchantUid, amount);

        return VirtualAccountInfo.builder()
                .accountNumber("0123456789")  // 테스트용 계좌번호
                .bankCode("088")              // 신한은행 코드
                .bankName("신한은행")          // 은행명
                .accountHolder("원티드마켓")    // 예금주
                .dueDate(LocalDateTime.now().plusDays(7))  // 7일 후 만료
                .build();
    }

    // 결제 검증 시뮬레이션
    public boolean validatePayment(String impUid, String merchantUid, Long amount) {
        // 실제 검증 대신 로깅만 수행
        log.info("Validating payment - impUid: {}, merchantUid: {}, amount: {}",
                impUid, merchantUid, amount);

        // 테스트를 위해 항상 true 반환
        return true;
    }

    // 결제 취소 시뮬레이션
    public void cancelPayment(String impUid, String reason) {
        // 실제 취소 대신 로깅만 수행
        log.info("Cancelling payment - impUid: {}, reason: {}", impUid, reason);
    }
}
