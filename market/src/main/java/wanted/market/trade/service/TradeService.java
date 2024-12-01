package wanted.market.trade.service;


import com.siot.IamportRestClient.exception.IamportResponseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wanted.market.item.domain.dto.response.ItemListSearchResponseDto;
import wanted.market.item.domain.entity.Item;
import wanted.market.item.domain.entity.ItemStatus;
import wanted.market.item.repository.ItemRepository;
import wanted.market.member.repository.MemberRepository;
import wanted.market.portone.service.PortoneService;
import wanted.market.trade.domain.dto.request.TradeRequestDto;
import wanted.market.trade.domain.dto.response.TradeBuyResponseDto;
import wanted.market.trade.domain.dto.response.TradeMyBuyListDto;
import wanted.market.trade.domain.dto.response.TradeMySellListDto;
import wanted.market.trade.domain.entity.AcceptStatus;
import wanted.market.trade.domain.entity.Trade;
import wanted.market.trade.domain.entity.TradeStatus;
import wanted.market.trade.repository.TradeRepository;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static wanted.market.portone.service.PortoneService.*;
import static wanted.market.trade.domain.entity.AcceptStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TradeService {
    private final TradeRepository tradeRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final PortoneService portoneService;


    /**
     * 구매 요청 ->
     * itemStatus : quantity > 0 : ONSALE
     * tradeStatus : buy
     *
     * @param itemId  : ITEM, sellerId
     * @param buyerId
     * @return
     */
    @Transactional
    public TradeBuyResponseDto buyItem(Long itemId, Long buyerId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new RuntimeException("아이템 못찾음"));
        if (!item.getStatus().equals(ItemStatus.ONSALE)) {
            throw new RuntimeException("판매중이 아니라서 구매 요청 불가");
        }



        Trade trade = Trade.builder()
                .status(TradeStatus.BUY)
                .price(item.getPrice())
                .item(item)
                .seller(memberRepository.findById(item.getSeller().getId())
                        .orElseThrow(() -> new RuntimeException("판매자 정보를 찾을 수 없음")))
                .buyer(memberRepository.findById(buyerId)
                        .orElseThrow(() -> new RuntimeException("구매자 정보를 찾을 수 없음")))
                .build();



        Trade savedTrade = tradeRepository.save(trade);

        try {
            String merchantUid = portoneService.prePurchase(BigDecimal.valueOf(savedTrade.getPrice()));
            savedTrade.setMerchantUid(merchantUid);
        } catch (IamportResponseException e) {
            throw new RuntimeException("IamPort 연결 실패");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        return new TradeBuyResponseDto(savedTrade.getId(), savedTrade.getItem().getId(), savedTrade.getStatus());
    }

    /**
     * 판매승인
     */
    @Transactional
    public TradeBuyResponseDto buyAccept(Long tradeId, AcceptStatus acceptStatus, Long sellerId) {
        Trade trade = tradeRepository.findById(tradeId).orElseThrow(()->new RuntimeException("거래를 찾을 수 없음"));
        if (!trade.getSeller().getId().equals(sellerId)) {
            throw new RuntimeException("판매 등록자와 판매승인자가 다름");
        }

        if (acceptStatus.equals(ACCEPT)) {
            trade.setStatusSELL();
            trade.getItem().itemQuantityDecrease();
        } else {
            trade.setStatusCancel();
            portoneService.refund(tradeId);
        }

        return new TradeBuyResponseDto(trade.getId(), trade.getItem().getId(), trade.getStatus());
    }

    /**
     * 구매 확정
     */

    @Transactional
    public TradeBuyResponseDto buyConfirm(Long tradeId, Long buyerId) {
        Trade trade = tradeRepository.findById(tradeId).get();
        if (!trade.getBuyer().getId().equals(buyerId)) {
            throw new RuntimeException("구매 등록자와 구매 확정자가 다름");
        }
        trade.setStatusEND();
        Item item = trade.getItem();
        item.setStatusCOMPLETE();
        return new TradeBuyResponseDto(trade.getId(), trade.getItem().getId(), trade.getStatus());
    }

    @Transactional
    public List<TradeMyBuyListDto> myBuyList(Long myId) {
        List<Trade> myBuyTrades = tradeRepository.findByBuyerId(myId);
        return myBuyTrades.stream()
                .map(trade -> new TradeMyBuyListDto(
                        trade.getId(),
                        trade.getItem().getId(),
                        trade.getPrice(),
                        trade.getSeller().getId(),
                        trade.getSeller().getMemberName(),
                        trade.getStatus()
                )).toList();
    }

    @Transactional
    public List<TradeMySellListDto> mySellingList(Long myId) {
        List<Trade> mySellTrades = tradeRepository.findBySellerId(myId);
//        log.info("myselltrades = {}", mySellTrades.get(0).getId());
        return mySellTrades.stream()
                .map(trade -> new TradeMySellListDto(
                        trade.getId(),
                        trade.getItem().getId(),
                        trade.getPrice(),
                        trade.getBuyer().getId(),
                        trade.getBuyer().getMemberName(),
                        trade.getStatus()
                )).toList();
    }
}