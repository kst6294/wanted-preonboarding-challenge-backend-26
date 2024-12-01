package com.wanted.payment.portone;


import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import com.wanted.payment.dto.PaymentCheckDto;
import com.wanted.payment.service.PaymentGatewayService;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class PortOneService implements PaymentGatewayService {
    private static final String BASE_URL = "https://api.iamport.kr";
    private IamportClient iamportClient;

    @Value("${port-one.imp.key}")
    private String IMP_KEY;
    @Value("${port-one.imp.secret}")
    private String IMP_SECRET;

    @PostConstruct
    public void initializeClient() {
        iamportClient = new IamportClient(IMP_KEY, IMP_SECRET);
    }

    @Override
    public void paymentCancel() {
        String impId = "";
        // 부분결제 여부
//        iamportClient.cancelPaymentByImpUid(new CancelData(impId, true, new BigDecimal(1)));
        //imp_uid?: string포트원 거래고유번호
        //(Optional)
        //merchant_uid?: string고객사 주문번호
        //(Optional)
        //amount?: number(부분)취소 요청금액
    }

    @Transactional
    public boolean paymentCheck(PaymentCheckDto dto) {
        IamportResponse<Payment> iamportResponse = null;
        try {
            iamportResponse = iamportClient.paymentByImpUid(dto.getPaymentId());
        } catch (IamportResponseException | IOException e) {
            throw new RuntimeException("portOne 결제 확인 중 에러가 발생하였습니다. " + e.getMessage());
        }

        return iamportResponse != null && iamportResponse.getResponse().getStatus().equals("paid");
    }

    public void 가상계좌(int orderId) {

        IamportClient client;
        // 가상계좌 발급 api
        // pg_api_key값 필요
        // merchant_uid: string고객사 주문번호 (이건 랜덤으로 가져오기 - orderId를 기준으로.)
        //amount: number입금 예정 금액  (이것도.)
        // pg?: stringPG사 구분코드
    }
}
