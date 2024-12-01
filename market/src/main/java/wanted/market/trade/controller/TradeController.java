package wanted.market.trade.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wanted.market.global.util.BaseController;
import wanted.market.trade.domain.dto.request.AcceptRequest;
import wanted.market.trade.domain.dto.response.TradeBuyResponseDto;
import wanted.market.trade.domain.dto.response.TradeMyBuyListDto;
import wanted.market.trade.domain.dto.response.TradeMySellListDto;
import wanted.market.trade.service.TradeService;

import java.util.List;

import static wanted.market.global.util.BaseController.*;

@Tag(name = "거래 기능", description = "구매요청, 구매승인, 구매확정 3단계")
@RestController
@RequiredArgsConstructor
@RequestMapping("/trade")
public class TradeController {
    private final TradeService tradeService;

    /**
     * 구매하기 - buyRequest
     * 판매승인 - buyAccept
     * 구매승인 - buyConfirm
     * <p>
     * 내가 구매한 용품 목록 - myBuyList
     * 내가 판매중인 용품 목록 - mySellingList
     */

    @PostMapping("/buy/{itemId}")
    public ResponseEntity<TradeBuyResponseDto> buyRequest(@RequestParam("itemId") Long itemId, HttpServletRequest request) {
        return ResponseEntity.ok(tradeService.buyItem(itemId, getMemberIdFromSession(request)));
    }

    @PostMapping("/accept/{tradeId}")
    public ResponseEntity<TradeBuyResponseDto> buyAccept(@RequestParam("tradeId") Long tradeId, @RequestBody AcceptRequest acceptRequest, HttpServletRequest request) {
        return ResponseEntity.ok(tradeService.buyAccept(tradeId, acceptRequest.getAcceptStatus(), getMemberIdFromSession(request)));
    }

    @PostMapping("/confirm/{tradeId}")
    public ResponseEntity<TradeBuyResponseDto> buyConfirm(@RequestParam("tradeId") Long tradeId, HttpServletRequest request) {
        return ResponseEntity.ok(tradeService.buyConfirm(tradeId, getMemberIdFromSession(request)));
    }

    @GetMapping("/my-buy-list")
    public ResponseEntity<List<TradeMyBuyListDto>> myBuyList(HttpServletRequest request) {
        return ResponseEntity.ok(tradeService.myBuyList(getMemberIdFromSession(request)));
    }

    @GetMapping("my-selling-list")
    public ResponseEntity<List<TradeMySellListDto>> mySellingList(HttpServletRequest request) {
        return ResponseEntity.ok(tradeService.mySellingList(getMemberIdFromSession(request)));
    }
}
