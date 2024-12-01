package org.wantedpayment.trade.controller;

import com.siot.IamportRestClient.exception.IamportResponseException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wantedpayment.trade.domain.dto.request.*;
import org.wantedpayment.portone.model.dto.response.PreparationResponse;
import org.wantedpayment.global.util.CommonController;
import org.wantedpayment.trade.domain.dto.response.BuyHistoryResponse;
import org.wantedpayment.trade.domain.dto.response.SellHistoryResponse;
import org.wantedpayment.trade.service.TradeService;

import java.io.IOException;

@RestController
@RequestMapping("/trade")
@RequiredArgsConstructor
public class TradeController extends CommonController {
    private final TradeService tradeService;

    @PostMapping
    public ResponseEntity<PreparationResponse> buyItem(@RequestBody TradeCreateRequest request,
                                                       HttpServletRequest httpServletRequest)
            throws IamportResponseException, IOException {
        return ResponseEntity.ok(
                tradeService.buyItem(
                        request,
                        getLoginMemberId(httpServletRequest)
                )
        );
    }

    @PatchMapping("/cancel")
    public void cancelTrade(@RequestBody CancelPurchaseRequest request, HttpServletRequest httpServletRequest){
        tradeService.cancelTrade(request, getLoginMemberId(httpServletRequest));
    }

    @PatchMapping("/refuse")
    public void refuseTrade(@RequestBody RefuseTradeRequest request, HttpServletRequest httpServletRequest){
        tradeService.refuseTrade(request, getLoginMemberId(httpServletRequest));
    }

    @PostMapping("/accept")
    public void acceptTrade(@RequestBody TradeAcceptRequest request, HttpServletRequest httpServletRequest) {
        tradeService.acceptTrade(request, getLoginMemberId(httpServletRequest));
    }

    @PostMapping("/confirm")
    public void confirmPurchase(@RequestBody TradeConfirmRequest request, HttpServletRequest httpServletRequest) {
        tradeService.confirmPurchase(request, getLoginMemberId(httpServletRequest));
    }

    @GetMapping("/buy-history")
    public ResponseEntity<Page<BuyHistoryResponse>> showBuyHistory(Pageable pageable, HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok(tradeService.showBuyHistory(pageable, getLoginMemberId(httpServletRequest)));
    }

    @GetMapping("/sell-history")
    public ResponseEntity<Page<SellHistoryResponse>> showSellHistory(Pageable pageable, HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok(tradeService.showSellHistory(pageable, getLoginMemberId(httpServletRequest)));
    }
}
