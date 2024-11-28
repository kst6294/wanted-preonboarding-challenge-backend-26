package hwijae.portonepayment.domain.service;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import hwijae.portonepayment.domain.entity.Order;
import hwijae.portonepayment.domain.entity.PaymentStatus;
import hwijae.portonepayment.domain.repository.OrderRepository;
import hwijae.portonepayment.domain.repository.PaymentRepository;
import hwijae.portonepayment.web.request.PaymentCallbackRequest;
import hwijae.portonepayment.web.request.RequestPayDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



import java.io.IOException;
import java.math.BigDecimal;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final IamportClient iamportClient;

    @Override
    public RequestPayDto findRequestDto(String orderUid) {

        Order order = (Order) orderRepository.findOrderAndPaymentAndMember(orderUid)
                .orElseThrow(() -> new IllegalArgumentException("주문이 없습니다."));

        return RequestPayDto.builder()
                .buyerName(order.getMember().getUsername())
                .buyerEmail(order.getMember().getEmail())
                .paymentPrice(order.getPayment().getPrice())
                .itemName(order.getItemName())
                .orderUid(order.getOrderUid())
                .build();
    }

    @Override
    public IamportResponse<Payment> paymentByCallback(PaymentCallbackRequest request) {

        try {
            // 결제 단건 조회(아임포트)
            IamportResponse<Payment> iamportResponse = iamportClient.paymentByImpUid(request.getPaymentUid());
            // 주문내역 조회
            Order order = (Order) orderRepository.findOrderAndPayment(request.getOrderUid())
                    .orElseThrow(() -> new IllegalArgumentException("주문 내역이 없습니다."));

            // 결제 완료가 아니면
            if(!iamportResponse.getResponse().getStatus().equals("paid")) {
                // 주문, 결제 삭제
                orderRepository.delete(order);
                paymentRepository.delete(order.getPayment());

                throw new RuntimeException("결제 미완료");
            }

            // DB에 저장된 결제 금액
            Long price = order.getPayment().getPrice();
            // 실 결제 금액
            int iamportPrice = iamportResponse.getResponse().getAmount().intValue();

            // 결제 금액 검증
            if(iamportPrice != price) {
                // 주문, 결제 삭제
                orderRepository.delete(order);
                paymentRepository.delete(order.getPayment());

                // 결제 취소(아임포트)
                iamportClient.cancelPaymentByImpUid(new CancelData(iamportResponse.getResponse().getImpUid(), true, new BigDecimal(iamportPrice)));

                throw new RuntimeException("결제금액 위변조 의심");
            }

            // 결제 상태 변경
            order.getPayment().changePaymentBySuccess(PaymentStatus.OK, iamportResponse.getResponse().getImpUid());

            return iamportResponse;

        } catch (IamportResponseException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 결제 취소 메서드
    @Override
    public void cancelPayment(String orderUid) {
        try {
            // 주문을 주문 번호(orderUid)로 조회
            Order order = orderRepository.findByOrderUid(orderUid)
                    .orElseThrow(() -> new IllegalArgumentException("해당 주문을 찾을 수 없습니다."));

            // 결제 상태가 '결제 완료' 상태여야만 취소가 가능
            if (!order.getPayment().getStatus().equals(PaymentStatus.OK)) {
                throw new IllegalArgumentException("취소할 수 없는 결제 상태입니다.");
            }

            // 아임포트 API를 통해 결제 취소 요청
            String paymentUid = order.getPayment().getPaymentUid();
            IamportResponse<Payment> response = iamportClient.cancelPaymentByImpUid(
                    new CancelData(paymentUid, true, new BigDecimal(order.getPrice()))
            );

            // 결제 취소가 성공하면 결제 상태를 'CANCELLED'로 업데이트
            if (response.getResponse() != null) {
                // 결제 상태 변경
                order.getPayment().changePaymentBySuccess(PaymentStatus.CANCEL, response.getResponse().getImpUid());
                // 변경된 결제 상태 DB에 반영
                orderRepository.save(order);
            }

        } catch (Exception e) {
            throw new RuntimeException("결제 취소에 실패했습니다.");
        }
    }
}
