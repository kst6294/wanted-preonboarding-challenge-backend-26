package wanted.market.portone.service;

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
import org.springframework.beans.factory.CannotLoadBeanClassException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wanted.market.portone.domain.WebHook;
import wanted.market.trade.domain.dto.response.RefundMessageResponse;
import wanted.market.trade.domain.entity.Trade;
import wanted.market.trade.repository.TradeRepository;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PortoneService {

    private final TradeRepository tradeRepository;

    @Value("${imp_key}")
    private String IMP_KEY;

    @Value("${imp_secret}")
    private String IMP_SECRET;

    private IamportClient iamportClient;

    @PostConstruct
    public void initializeImportClient() {
        iamportClient = new IamportClient(IMP_KEY, IMP_SECRET);
    }

    public String prePurchase(BigDecimal amount) throws IamportResponseException, IOException {
        String merchantUid = generateMerchantUid();
        log.info("merchantUid = {}", merchantUid);

        IamportResponse<Prepare> prepareIamportResponse = iamportClient.postPrepare(new PrepareData(merchantUid, amount));

        log.info("return code : {}", prepareIamportResponse.getCode());
        log.info("return message : {}", prepareIamportResponse.getMessage());

        if (prepareIamportResponse.getCode() != 0) {
            throw new RuntimeException(prepareIamportResponse.getMessage());
        }
        return merchantUid;
    }

    public RefundMessageResponse refund(Long tradeId) {
        Trade refundTrade = tradeRepository.findById(tradeId).orElseThrow(() -> new RuntimeException("환불 요청에 대한 해당 거래를 찾을 수 없음"));
        String merchantUid = refundTrade.getMerchantUid();
        try {
            iamportClient.cancelPaymentByImpUid(new CancelData(merchantUid, false));
        } catch (IamportResponseException e) {
            throw new RuntimeException("port one 결제 취소 실패");
        } catch (IOException e) {
            throw new RuntimeException("취소 하고자 하는 거래 데이터를 찾을 수 없음");
        }

        RefundMessageResponse response = RefundMessageResponse.builder()
                .refundTradeId(tradeId)
                .refundAmount(refundTrade.getPrice())
                .refundReason("판매자에 의한 취소")
                .build();
        return response;
    }


    /**
     * private String imp_uid; // : 결제번호
     * private String merchant_uid; //: 주문번호
     * private String status; //: 결제 결과
     * private String cancellation_id;
     *
     * @return
     */

    @Transactional
    public void validateWebHook(WebHook webHook) {
        IamportResponse<Payment> paymentIamportResponse;

        try {
           paymentIamportResponse = iamportClient.paymentByImpUid(webHook.getImp_uid());
        } catch (IamportResponseException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Trade findTrade = tradeRepository.findByMerchantUid(paymentIamportResponse.getResponse().getMerchantUid()).orElseThrow(() -> new RuntimeException("web hook 과 일치하는 거래 내역 없음."));
        findTrade.setStatusPay();
        if (!BigDecimal.valueOf(findTrade.getPrice()).equals(paymentIamportResponse.getResponse().getAmount())) {
            try {
                CancelData cancelData = new CancelData(findTrade.getMerchantUid(), false);
                IamportResponse<Payment> response = iamportClient.cancelPaymentByImpUid(cancelData);
            } catch (IamportResponseException e) {
                throw new RuntimeException("port one 결제 취소 실패");
            } catch (IOException e) {
                throw new RuntimeException("취소 하고자 하는 데이터 찾을 수 없음");
            } finally {
                findTrade.setStatusCancel();
            }
        }

        log.info("web hook 인증 결과 이상 없음.");
    }


    public static String generateMerchantUid() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmss");
        String currentTime = dateFormat.format(new Date());
        String uuid = UUID.randomUUID().toString();
        byte[] uuidStringBytes = uuid.getBytes(StandardCharsets.UTF_8);
        byte[] hashBytes;

        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            hashBytes = messageDigest.digest(uuidStringBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            sb.append(String.format("%02x", hashBytes[i]));
        }

        return currentTime + sb.toString();
    }
}
