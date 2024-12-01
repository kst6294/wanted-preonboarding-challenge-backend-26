package org.wantedpayment.portone.service;


import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.request.PrepareData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import com.siot.IamportRestClient.response.Prepare;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.wantedpayment.member.repository.MemberRepository;
import org.wantedpayment.trade.domain.dto.request.CancelPurchaseRequest;
import org.wantedpayment.item.domain.entity.Item;
import org.wantedpayment.portone.model.dto.request.VBankRequest;
import org.wantedpayment.portone.model.dto.request.WebhookRequest;
import org.wantedpayment.portone.model.dto.response.VBankResponse;
import org.wantedpayment.trade.domain.dto.request.RefuseTradeRequest;
import org.wantedpayment.trade.domain.entity.Trade;
import org.wantedpayment.trade.repository.TradeRepository;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class PortOneService {
    private final String BASE_URI = "https://api.iamport.kr";
    private final String getAccessTokenURL = "/users/getToken";
    private final String cancelPurchaseURL = "/payments/cancel";
    private final MemberRepository memberRepository;

    @Value("${portone.api.key}")
    private String IMP_KEY;
    @Value("${portone.api.secret}")
    private String IMP_SECRET;
    @Value("${portone.id}")
    private String IMP_ID;

    private final TradeRepository tradeRepository;
    private final WebClient webClient = WebClient.builder()
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();

    private IamportClient iamportClient;

    @PostConstruct
    public void initializeIamportClient() {
        iamportClient = new IamportClient(IMP_KEY, IMP_SECRET);
    }

    public String preparePurchase(BigDecimal amount) throws IamportResponseException, IOException {
        String merchantUid = generateOrderNumber();
        if (tradeRepository.findByOrderNumber(merchantUid).isPresent()) {
            merchantUid = generateOrderNumber();
        }

        IamportResponse<Prepare> response = iamportClient.postPrepare(
                new PrepareData(merchantUid, amount)
        );

        log.info("결과 코드 : {}", response.getCode());
        log.info("결과 메시지 : {}", response.getMessage());

        if (response.getCode() != 0) {
            throw new RuntimeException(response.getMessage());
        }

        return merchantUid;
    }

    public void checkPurchaseCompleteWithClient(String impUid, Long memberId)
            throws IamportResponseException, IOException {
        log.info("Checking Purchase With Client...");

        IamportResponse<Payment> response = iamportClient.paymentByImpUid(impUid);

        Trade trade = tradeRepository.findByOrderNumber(response.getResponse().getMerchantUid())
                .orElseThrow(() -> new RuntimeException("Trade Not Found"));

        if (!Objects.equals(trade.getBuyer().getId(), memberId)) {
            throw new RuntimeException("User Has No Authority To Check Purchase");
        }

        if(!Objects.equals(trade.getTradePrice(), response.getResponse().getAmount())) {
            CancelData data = cancelData(new CancelPurchaseRequest(
                    trade.getId(),
                    impUid,
                    BigDecimal.valueOf(0)
//                    "주문 금액과 결제 금액이 일치하지 않습니다",
//                    "환불 예금주",
//                    "환불 계좌",
//                    "환불 은행"
            ));

            iamportClient.cancelPaymentByImpUid(data);

            trade.cancelPurchase();
            Item item = trade.getItem();
            item.increaseQuantity();

            throw new RuntimeException("Trade Price Not Matched");
        }

        if (!response.getResponse().getStatus().equals("paid")) {
            log.info("Client: Purchase Not Completed");
        }

        log.info("Client: Purchase Completed!");
    }

    public String checkPurchaseCompleteWithWebhook(WebhookRequest request)
            throws IamportResponseException, IOException {
        log.info("Checking Purchase With Webhook...");

        IamportResponse<Payment> response = iamportClient.paymentByImpUid(request.getImpUid());

        Trade trade = tradeRepository.findByOrderNumber(request.getMerchantUid())
                .orElseThrow(() -> new RuntimeException("Trade Not Found"));

        if(!Objects.equals(trade.getTradePrice(), response.getResponse().getAmount())) {
            CancelData data = cancelData(new CancelPurchaseRequest(
                    trade.getId(),
                    request.getImpUid(),
                    BigDecimal.valueOf(0)
//                    "주문 금액과 결제 금액이 일치하지 않습니다",
//                    "환불 예금주",
//                    "환불 계좌",
//                    "환불 은행"
            ));

            iamportClient.cancelPaymentByImpUid(data);

            trade.cancelPurchase();
            Item item = trade.getItem();
            item.increaseQuantity();

            throw new RuntimeException("Trade Price Not Matched");
        }

        if (response.getResponse().getStatus().equals("paid")) {
            log.info("Webhook: Purchase Completed!");
            return "Completed";
        } else if (response.getResponse().getStatus().equals("ready")) {
            log.info("Webhook: Virtual Bank Account Is Ready");
            return "VBank Ready";
        } else {
            log.info("Webhook: Purchase Not Completed");
            return "Not Completed";
        }
    }

    public VBankResponse vBankPurchase(VBankRequest request, Long memberId) throws IamportResponseException, IOException {
        memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member Not Found"));

        ResponseEntity<Payment> response = webClient.post()
                .uri(BASE_URI + "/vbanks")
                .header("Authentication", iamportClient.getAuth().getResponse().getToken())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve() // client message 전송
                .toEntity(Payment.class)
                .block();

        if (response == null || response.getStatusCode().isError()) {
            throw new RuntimeException("Failed To Create Virtual Bank Account");
        }

        return new VBankResponse(
                Objects.requireNonNull(response.getBody()).getImpUid(),
                response.getBody().getMerchantUid(),
                response.getBody().getAmount(),
                response.getBody().getCancelAmount(),
                response.getBody().getCurrency(),
                response.getBody().getStatus()
        );
    }

    // 구매자의 결제 취소
    public void cancelPurchase(CancelPurchaseRequest request) {
        log.info("Cancel Purchase..");

        CancelData data = cancelData(request);

        //결제 취소
        try {
            IamportResponse<Payment> response = iamportClient.cancelPaymentByImpUid(data);
            log.info(response.getMessage());
        } catch (IamportResponseException | IOException e) {
            throw new RuntimeException("Cancel Purchase Failed: " + e.getMessage());
        }

        log.info("Cancel Purchase Completed!");
    }

    // 판매자의 결제 취소
    public void refusePurchase(RefuseTradeRequest request) {
        log.info("Reject Purchase..");

        CancelData data = cancelData(request);

        //결제 취소
        try {
            IamportResponse<Payment> response = iamportClient.cancelPaymentByImpUid(data);
            log.info(response.getMessage());
        } catch (IamportResponseException | IOException e) {
            throw new RuntimeException("Cancel Purchase Failed: " + e.getMessage());
        }

        log.info("Refuse Purchase Completed!");
    }

    private CancelData cancelData(CancelPurchaseRequest request) {
        CancelData data;

        if (request.getAmount().compareTo(BigDecimal.ZERO) == 0) {
            //전액 환불
            data = new CancelData(request.getImpUid(), true);
        } else {
            //부분 환불
            data = new CancelData(request.getImpUid(), true, request.getAmount());
        }
//
//        data.setReason(request.getReason());
//        data.setRefund_bank(request.getRefundBank());
//        data.setRefund_account(request.getRefundAccount());
//        data.setRefund_holder(request.getRefundHolder());

        return data;
    }

    private CancelData cancelData(RefuseTradeRequest request) {
        CancelData data;

        if (request.getAmount().compareTo(BigDecimal.ZERO) == 0) {
            //전액 환불
            data = new CancelData(request.getImpUid(), true);
        } else {
            //부분 환불
            data = new CancelData(request.getImpUid(), true, request.getAmount());
        }

        return data;
    }

    public static String generateOrderNumber() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String currentTime = dateFormat.format(new Date());
        String randomNumber = generateRandomNumber(6); // 주문번호의 랜덤한 숫자 부분 길이 (여기서는 6자리로 설정)

        return currentTime + randomNumber;
    }

    public static String generateRandomNumber(int length) {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            int randomNumber = random.nextInt(10);
            sb.append(randomNumber);
        }

        return sb.toString();
    }
}
