package org.wantedpayment.trade.service;

import com.siot.IamportRestClient.exception.IamportResponseException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.wantedpayment.portone.service.PortOneService;
import org.wantedpayment.portone.model.dto.request.CancelPurchaseRequest;
import org.wantedpayment.portone.model.dto.response.PreparationResponse;
import org.wantedpayment.item.domain.entity.Item;
import org.wantedpayment.item.domain.entity.ItemStatus;
import org.wantedpayment.item.repository.ItemRepository;
import org.wantedpayment.member.domain.entity.Member;
import org.wantedpayment.member.repository.MemberRepository;
import org.wantedpayment.trade.domain.dto.request.CheckPurchaseRequest;
import org.wantedpayment.trade.domain.dto.request.TradeAcceptRequest;
import org.wantedpayment.trade.domain.dto.request.TradeConfirmRequest;
import org.wantedpayment.trade.domain.dto.request.TradeCreateRequest;
import org.wantedpayment.trade.domain.dto.response.BuyHistoryResponse;
import org.wantedpayment.trade.domain.dto.response.SellHistoryResponse;
import org.wantedpayment.trade.domain.entity.Trade;
import org.wantedpayment.trade.domain.entity.TradeStatus;
import org.wantedpayment.trade.repository.TradeRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Slf4j
@RequiredArgsConstructor
public class TradeService {
    private final TradeRepository tradeRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final PortOneService portOneService;

    @Transactional
    public PreparationResponse buyItem(TradeCreateRequest request, Long memberId) throws IamportResponseException, IOException {
        // 사전 과제 1과 달리 거래내역이 존재해도 거래할 수 있게 만들었음
//        if(tradeRepository.findByItemIdAndBuyerId(request.getItemId(), memberId).isPresent()) {
//            throw new RuntimeException("Trade history already exists");
//        }

        Member buyer = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new RuntimeException("Item not found"));

        String orderNumber = portOneService.preparePurchase(item.getPrice());

        Member seller = item.getMember();

        Trade trade = Trade.builder()
                .tradeStatus(TradeStatus.REQUESTED)
                .tradePrice(item.getPrice())
                .paymentDateTime(LocalDateTime.now())
                .orderNumber(orderNumber)
                .item(item)
                .buyer(buyer)
                .seller(seller)
                .build();

        tradeRepository.save(trade);

        return new PreparationResponse(trade.getOrderNumber());
    }

    @Transactional
    public void cancelTrade(CancelPurchaseRequest request, Long userId){
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        Trade trade = tradeRepository.findById(request.getTradeId())
                .orElseThrow(() -> new RuntimeException("Trade not found"));

        if (!trade.getBuyer().getId().equals(member.getId())) {
            throw new RuntimeException("Login member does not match");
        }

        if(trade.getTradeStatus() != TradeStatus.REQUESTED) {
            throw new RuntimeException("Trade Cannot be Cancelled");
        }

        portOneService.cancelPurchase(request);
        trade.cancelPurchase();
    }

    @Transactional
    public void acceptTrade(TradeAcceptRequest request, Long memberId) {
        Trade trade = tradeRepository.findById(request.getTradeId())
                .orElseThrow(() -> new RuntimeException("Trade not found"));
        Item item = trade.getItem();

        Member seller = trade.getSeller();

        if(!Objects.equals(seller.getId(), memberId)) {
            throw new RuntimeException("User not authorized to accept trade");
        }

        trade.acceptTrade();
        item.decreaseQuantity();

        if(item.getQuantity() == 0) {
            item.changeStatus(ItemStatus.RESERVED);
        }
    }

    @Transactional
    public void confirmPurchase(TradeConfirmRequest request, Long memberId) {
        Trade trade = tradeRepository.findById(request.getTradeId())
                .orElseThrow(() -> new RuntimeException("Trade not found"));
        Item item = trade.getItem();

        Member buyer = trade.getBuyer();

        if(!Objects.equals(buyer.getId(), memberId)) {
            throw new RuntimeException("User not authorized to confirm purchase");
        }

        if(!trade.getTradeStatus().equals(TradeStatus.ACCEPTED)) {
            throw new RuntimeException("Trade is not accepted");
        }

        trade.confirmPurchase();

        if(item.getQuantity() == 0 && checkAllTradeConfirmed(item)) {
            item.changeStatus(ItemStatus.COMPLETED);
        }
    }

    public boolean checkAllTradeConfirmed(Item item) {
        List<Trade> trades = tradeRepository.findByItemId(item.getId());
        AtomicBoolean allTradeConfirmed = new AtomicBoolean(true);

        trades.forEach(currTrade -> {
            if (!currTrade.getTradeStatus().equals(TradeStatus.CONFIRMED)) {
                allTradeConfirmed.set(false);
            }
        });

        return allTradeConfirmed.get();
    }

    public Page<BuyHistoryResponse> showBuyHistory(Pageable pageable, Long memberId) {
        memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        return getBuyHistory(pageable, memberId);
    }

    public Page<SellHistoryResponse> showSellHistory(Pageable pageable, Long memberId) {
        memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        return getSellHistory(pageable, memberId);
    }

    private Page<BuyHistoryResponse> getBuyHistory(Pageable pageable, Long memberId) {
        Page<Trade> trades = tradeRepository.findByBuyerId(pageable, memberId);

        return trades.map(
                currTrade ->
                        new BuyHistoryResponse(
                                currTrade.getId(),
                                currTrade.getTradePrice(),
                                currTrade.getTradeStatus(),
                                currTrade.getItem().getId(),
                                currTrade.getItem().getName(),
                                currTrade.getSeller().getId(),
                                currTrade.getSeller().getName()
                        )
        );
    }

    private Page<SellHistoryResponse> getSellHistory(Pageable pageable, Long memberId) {
        Page<Trade> trades = tradeRepository.findBySellerId(pageable, memberId);

        return trades.map(
                currTrade ->
                        new SellHistoryResponse(
                                currTrade.getId(),
                                currTrade.getTradePrice(),
                                currTrade.getTradeStatus(),
                                currTrade.getItem().getId(),
                                currTrade.getItem().getName(),
                                currTrade.getBuyer().getId(),
                                currTrade.getBuyer().getName()
                        )
        );
    }
}
