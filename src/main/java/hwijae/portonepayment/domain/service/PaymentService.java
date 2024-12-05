package hwijae.portonepayment.domain.service;

import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import hwijae.portonepayment.web.request.PaymentCallbackRequest;
import hwijae.portonepayment.web.request.RequestPayDto;


public interface PaymentService {
    // 결제 요청 데이터 조회
    RequestPayDto findRequestDto(String orderUid);
    // 결제(콜백)
    IamportResponse<Payment> paymentByCallback(PaymentCallbackRequest request);

    // 결제 취소 메서드
    void cancelPayment(String orderUid);
}
