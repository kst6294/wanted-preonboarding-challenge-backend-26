package com.wanted.payment.portone;


import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import com.wanted.payment.dto.PaymentCheckDto;
import com.wanted.payment.dto.VirtualAccountInfoDto;
import com.wanted.payment.service.PaymentGatewayService;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.math.BigDecimal;

import static org.springframework.http.MediaType.APPLICATION_JSON;

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

    @Override
    public VirtualAccountInfoDto createVirtualAccount() {

        RestClient restClient = RestClient.create();
        CreateVirtualAccountRq rq = new CreateVirtualAccountRq();

        ResponseEntity<CreateVirtualAccountRs> result = restClient.post()
                .uri(BASE_URL + "vbanks")
                .contentType(APPLICATION_JSON)
                .body(rq)
                .retrieve()
//                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
//            throw new MyCustomRuntimeException(response.getStatusCode(), response.getHeaders())
//        })
                .toEntity(CreateVirtualAccountRs.class);

        // 가상계좌 발급 api
        // pg_api_key값 필요
        // merchant_uid: string고객사 주문번호 (이건 랜덤으로 가져오기 - orderId를 기준으로.)
        //amount: number입금 예정 금액  (이것도.)
        // pg?: stringPG사 구분코드

        return new VirtualAccountInfoDto();

    }

    @Override
    public void paymentCancel() {
        String impId="";
        try {
            iamportClient.cancelPaymentByImpUid(new CancelData(impId, true, new BigDecimal(1)));
        } catch (IamportResponseException | IOException e) {
            throw new RuntimeException(e);
        }
        //imp_uid?: string포트원 거래고유번호
        //(Optional)
        //merchant_uid?: string고객사 주문번호
        //(Optional)
        //amount?: number(부분)취소 요청금액
    }
}
