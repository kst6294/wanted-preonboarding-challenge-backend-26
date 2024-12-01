package org.wantedpayment.item.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.wantedpayment.item.domain.dto.request.ItemRegisterRequest;
import org.wantedpayment.item.domain.dto.response.ItemDetailResponse;
import org.wantedpayment.item.domain.dto.response.ItemPreviewResponse;
import org.wantedpayment.item.domain.entity.Item;
import org.wantedpayment.item.domain.entity.ItemStatus;
import org.wantedpayment.item.repository.ItemRepository;
import org.wantedpayment.member.repository.MemberRepository;
import org.wantedpayment.trade.repository.TradeRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final TradeRepository tradeRepository;

    public Page<ItemPreviewResponse> showAllItems(Pageable pageable) {
        return getAllItems(itemRepository.findAll(pageable));
    }

    public ItemDetailResponse showItem(Long itemId) {
        return getItemDetail(
                itemRepository.findById(itemId)
                        .orElseThrow(() -> new RuntimeException("Item not found"))
        );
    }

    @Transactional
    public void register(ItemRegisterRequest request, Long memberId) {
        log.info("User info: {}", memberId);

        Item item = Item.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .quantity(request.getQuantity())
                .member(memberRepository.findById(memberId)
                        .orElseThrow(() -> new RuntimeException("Member not found")))
                .status(ItemStatus.ON_SALE)
                .build();

        itemRepository.save(item);
    }

    @Transactional
    public void update(Long itemId, ItemRegisterRequest request, Long memberId) {
        log.info("User info: {}", memberId);

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        if(item.getStatus() != ItemStatus.ON_SALE) throw new RuntimeException("Not available to update item(It's not on sale)");

        item.updateItem(
                request.getName(),
                request.getDescription(),
                request.getPrice(),
                request.getQuantity()
        );
    }

    private Page<ItemPreviewResponse> getAllItems(Page<Item> items) {
        return items.map(
                currItem ->
                new ItemPreviewResponse(
                        currItem.getId(),
                        currItem.getName(),
                        currItem.getPrice(),
                        currItem.getQuantity(),
                        currItem.getStatus()
                ));
    }

    private ItemDetailResponse getItemDetail(Item item) {
        return new ItemDetailResponse(
                item.getId(),
                item.getMember().getName(),
                item.getName(),
                item.getDescription(),
                item.getPrice(),
                item.getQuantity(),
                item.getStatus()
        );
    }
}
