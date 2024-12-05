package hwijae.portonepayment.web.controller;

import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import hwijae.portonepayment.domain.entity.Order;
import hwijae.portonepayment.domain.repository.OrderRepository;
import hwijae.portonepayment.domain.service.PaymentService;
import hwijae.portonepayment.web.request.PaymentCallbackRequest;
import hwijae.portonepayment.web.request.RequestPayDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@Controller
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final OrderRepository orderRepository;

    @GetMapping("/payment/{orderUid}")
    public String paymentPage(@PathVariable(name = "orderUid", required = false) String orderUid,
                              Model model) {

        RequestPayDto requestDto = paymentService.findRequestDto(orderUid);
        model.addAttribute("requestDto", requestDto);

        return "payment";
    }

    @ResponseBody
    @PostMapping("/payment")
    public ResponseEntity<IamportResponse<Payment>> validationPayment(@RequestBody PaymentCallbackRequest request) {
        IamportResponse<Payment> iamportResponse = paymentService.paymentByCallback(request);

        log.info("결제 응답={}", iamportResponse.getResponse().toString());
        return new ResponseEntity<>(iamportResponse, HttpStatus.OK);

    }


    @GetMapping("/success-payment")
    public String successPaymentPage() {
        return "success-payment";
    }

    @GetMapping("/fail-payment")
    public String failPaymentPage() {
        return "fail-payment";
    }

    // 결제 목록 화면
    @GetMapping("/payment/list")
    public String paymentListPage(Model model) {
        // 예시: 회원 ID로 결제 목록을 조회
        Long memberId = 1L;  // 테스트용. 실제로는 로그인된 사용자 ID로 가져와야 함
        List<Order> orders = orderRepository.findAllByMemberId(memberId);
        model.addAttribute("orders", orders);
        return "payment-list";  // 결제 목록 페이지
    }

    // 결제 취소 요청
    @PostMapping("/payment/cancel")
    @ResponseBody
    public ResponseEntity<String> cancelPayments(@RequestBody List<String> orderUids) {
        try {
            // 선택된 주문번호로 결제 취소 처리
            for (String orderUid : orderUids) {
                paymentService.cancelPayment(orderUid); // 결제 취소 로직
            }
            return ResponseEntity.ok("선택한 결제들이 취소되었습니다.");
        } catch (Exception e) {
            log.error("결제 취소 실패", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("결제 취소에 실패했습니다.");
        }
    }
}
