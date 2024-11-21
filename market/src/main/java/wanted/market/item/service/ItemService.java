package wanted.market.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wanted.market.item.domain.dto.request.ItemCreateRequestDto;
import wanted.market.item.domain.dto.request.ItemUpdateRequestDto;
import wanted.market.item.domain.dto.response.ItemCreateResponseDto;
import wanted.market.item.domain.dto.response.ItemInfoResponseDto;
import wanted.market.item.domain.dto.response.ItemListSearchResponseDto;
import wanted.market.item.domain.dto.response.ItemUpdateResponseDto;
import wanted.market.item.domain.entity.Item;
import wanted.market.item.domain.entity.ItemStatus;
import wanted.market.item.repository.ItemRepository;
import wanted.market.member.repository.MemberRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public ItemCreateResponseDto registerItem(ItemCreateRequestDto itemDto, Long memberId) {
        Item item = Item.builder()
                .itemName(itemDto.getItemName())
                .price(itemDto.getPrice())
                .quantity(itemDto.getQuantity())
                .seller(memberRepository.findById(memberId)
                        .orElseThrow(()-> new RuntimeException("member not found")))
                .status(ItemStatus.ONSALE)
                .build();
        itemRepository.save(item);
        return new ItemCreateResponseDto(true, item.getId(), item.getItemName());
    }

    @Transactional
    public void deleteItem(Long itemId, Long memberId) {
        Item deleteItem = itemRepository.findById(itemId).orElseThrow(() -> new RuntimeException("상품을 찾을 수 없음"));
        if (!deleteItem.getSeller().getId().equals(memberId)) {
            throw new RuntimeException("상품 등록한 사람과 삭제 요청 사람이 다름");
        }
        itemRepository.deleteById(itemId);
    }

    @Transactional
    public ItemUpdateResponseDto updateItem(ItemUpdateRequestDto itemDto, Long memberId) {
        Item updateItem = itemRepository.findById(itemDto.getItemId()).orElseThrow(() -> new RuntimeException("상품을 찾을 수 없음"));
        if (!updateItem.getSeller().getId().equals(memberId)) {
            throw new RuntimeException("상품 등록한 사람과 수정 요청 사람이 다름");
        }
        updateItem.setItemName(itemDto.getItemName());
        updateItem.setPrice(itemDto.getPrice());
        updateItem.setQuantity(itemDto.getQuantity());
        return new ItemUpdateResponseDto(true, updateItem.getId(), updateItem.getItemName(), updateItem.getPrice(), updateItem.getQuantity());
    }

    @Transactional
    public List<ItemListSearchResponseDto> searchItems() {
        List<Item> items = itemRepository.findAll();

        return items.stream().map(
                item -> new ItemListSearchResponseDto(
                        item.getId(),
                        item.getItemName(),
                        item.getPrice(),
                        item.getQuantity(),
                        item.getStatus()
                )
        ).toList();
    }

    @Transactional
    public ItemInfoResponseDto infoItem(Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new RuntimeException("일치하는 상품을 찾을 수 없음"));
        return new ItemInfoResponseDto(item.getId(), item.getItemName(), item.getPrice(), item.getQuantity(),
                item.getStatus(), item.getSeller().getId(), item.getSeller().getMemberName());
    }
}
