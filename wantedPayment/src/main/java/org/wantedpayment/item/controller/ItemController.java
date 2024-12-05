package org.wantedpayment.item.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wantedpayment.global.util.CommonController;
import org.wantedpayment.item.domain.dto.request.ItemRegisterRequest;
import org.wantedpayment.item.domain.dto.response.ItemDetailResponse;
import org.wantedpayment.item.domain.dto.response.ItemPreviewResponse;
import org.wantedpayment.item.service.ItemService;

@Slf4j
@RestController
@RequestMapping("/item")
@RequiredArgsConstructor
public class ItemController extends CommonController {
    private final ItemService itemService;

    @GetMapping("/list")
    public ResponseEntity<Page<ItemPreviewResponse>> showAll(Pageable pageable) {
        return ResponseEntity.ok(itemService.showAllItems(pageable));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDetailResponse> showItem(@PathVariable Long itemId) {
        return ResponseEntity.ok(itemService.showItem(itemId));
    }

    @PostMapping
    public void registerItem(@RequestBody ItemRegisterRequest request, HttpServletRequest httpServletRequest) {
        itemService.register(request, getLoginMemberId(httpServletRequest));
    }

    @PatchMapping("/{itemId}")
    public void updateItem(@PathVariable Long itemId,
                           @RequestBody ItemRegisterRequest request,
                           HttpServletRequest httpServletRequest) {
        itemService.update(itemId, request, getLoginMemberId(httpServletRequest));
    }
}
