package wanted.market.item.controller;


import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wanted.market.item.domain.dto.request.ItemCreateRequestDto;
import wanted.market.item.domain.dto.request.ItemUpdateRequestDto;
import wanted.market.item.domain.dto.response.ItemCreateResponseDto;
import wanted.market.item.domain.dto.response.ItemInfoResponseDto;
import wanted.market.item.domain.dto.response.ItemListSearchResponseDto;
import wanted.market.item.domain.dto.response.ItemUpdateResponseDto;
import wanted.market.item.domain.entity.Item;
import wanted.market.item.service.ItemService;

import java.util.List;

import static wanted.market.global.util.BaseController.getMemberIdFromSession;

@Tag(name = "상품 기능", description = "상품 등록, 상품 수정, 상품 삭제, 상품 목록, 상품 상세")
@RestController
@RequestMapping("/item")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;


    /**
     * 등록 - "/create"
     * 삭제 - "/delete"
     * 수정 - "/update"
     * 목록 조회 - "/items"
     * 상품 상세 - "/info"
     */

    @PostMapping("/create")
    public ResponseEntity<ItemCreateResponseDto> createItem(ItemCreateRequestDto itemCreateRequestDto, HttpServletRequest request) {
        return ResponseEntity.ok(itemService.registerItem(itemCreateRequestDto, getMemberIdFromSession(request)));
    }

    @DeleteMapping("/delete")
    public void deleteItem(Long itemId, HttpServletRequest request) {
        itemService.deleteItem(itemId, getMemberIdFromSession(request));
    }

    @PatchMapping("/update")
    public ResponseEntity<ItemUpdateResponseDto> updateItem(ItemUpdateRequestDto updateDto, HttpServletRequest request) {
        return ResponseEntity.ok(itemService.updateItem(updateDto, getMemberIdFromSession(request)));
    }

    @GetMapping("/items")
    public ResponseEntity<List<ItemListSearchResponseDto>> searchItemList() {
        return ResponseEntity.ok(itemService.searchItems());
    }

    @GetMapping("/info/{id}")
    public ResponseEntity<ItemInfoResponseDto> infoItem(@RequestParam("id") Long itemId) {
        return ResponseEntity.ok(itemService.infoItem(itemId));
    }
}
