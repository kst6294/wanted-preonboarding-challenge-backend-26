package org.wantedpayment.portone.controller;

import com.siot.IamportRestClient.exception.IamportResponseException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wantedpayment.global.util.CommonController;
import org.wantedpayment.portone.model.dto.request.VBankRequest;
import org.wantedpayment.portone.model.dto.request.WebhookRequest;
import org.wantedpayment.portone.model.dto.response.VBankResponse;
import org.wantedpayment.portone.service.PortOneService;
import org.wantedpayment.trade.domain.dto.request.CheckPurchaseRequest;

import java.io.IOException;

@RestController
@RequestMapping("/portone")
@Slf4j
@RequiredArgsConstructor
public class PortOneController extends CommonController {
    private final PortOneService portOneService;

    @GetMapping("/purchase-check")
    public void checkPurchase(CheckPurchaseRequest request, HttpServletRequest httpServletRequest)
            throws IamportResponseException, IOException {

        portOneService.checkPurchaseCompleteWithClient(request.getImpUid(), getLoginMemberId(httpServletRequest));
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> receiveWebhook(@RequestBody WebhookRequest request)
            throws IamportResponseException, IOException {
        log.info("Received Webhook Request");

        return ResponseEntity.ok(portOneService.checkPurchaseCompleteWithWebhook(request));
    }

    @PostMapping("/vbank")
    public ResponseEntity<VBankResponse> purchaseByVBank(@RequestBody VBankRequest request, HttpServletRequest httpServletRequest)
            throws IamportResponseException, IOException {
        return ResponseEntity.ok(portOneService.vBankPurchase(request, getLoginMemberId(httpServletRequest)));
    }
}
