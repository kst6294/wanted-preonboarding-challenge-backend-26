package com.wanted.payment.portone;


import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import com.wanted.payment.dto.PaymentCheckDto;
import com.wanted.payment.dto.VirtualAccountCreateDto;
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
    @Value("${port-one.api.key}")
    private String API_KEY;
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
    public VirtualAccountInfoDto createVirtualAccount(VirtualAccountCreateDto dto) {
        RestClient restClient = RestClient.builder().baseUrl(BASE_URL+"/vbanks").build();
        CreateVirtualAccountRq rq = new CreateVirtualAccountRq(Integer.toString(dto.getOrderId()), dto.getPrice(), PGBankCode.KB.getCode(), 30);

        ResponseEntity<CreateVirtualAccountRs> rsEntity = restClient.post()
                .uri(builder -> builder.queryParam("pg_api_key", API_KEY).build())
                .contentType(APPLICATION_JSON)
                .body(rq)
                .retrieve()
                .toEntity(CreateVirtualAccountRs.class);

        if(!rsEntity.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("가상계좌 생성 중 오류 발생했습니다.");
        }

        CreateVirtualAccountRs rs = rsEntity.getBody();

        if(rs == null) {
            throw new RuntimeException("가상계좌가 정상적으로 생성되지 않았습니다.");
        }

        return new VirtualAccountInfoDto(rs.getImp_uid(), rs.getVbank_name(), rs.getVbank_num(), rs.getVbank_date());
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
