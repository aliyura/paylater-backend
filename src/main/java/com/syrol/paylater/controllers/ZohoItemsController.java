package com.syrol.paylater.controllers;

import com.syrol.paylater.pojos.APIResponse;
import com.syrol.paylater.pojos.zoho.ZohoItemRequest;
import com.syrol.paylater.pojos.zoho.ZohoMultiItemResponse;
import com.syrol.paylater.pojos.zoho.ZohoResponse;
import com.syrol.paylater.pojos.zoho.ZohoSingleItemResponse;
import com.syrol.paylater.services.ZohoItemService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import retrofit2.http.DELETE;
import retrofit2.http.PUT;

@RestController
@AllArgsConstructor
@RequestMapping(path = "api/v1/")
public class ZohoItemsController {

    private final ZohoItemService zohoItemService;

    @PostMapping("/items/add")
    public APIResponse<ZohoSingleItemResponse> addItem(@RequestBody ZohoItemRequest request) {
        return zohoItemService.createItem(request);
    }

    @PUT("/items/update/{itemId}")
    public APIResponse<ZohoSingleItemResponse> updateItem( @PathVariable String itemId, @RequestBody ZohoItemRequest request) {
        return zohoItemService.updateItem(request,itemId);
    }

    @PUT("/items/activate/{itemId}")
    public APIResponse<ZohoResponse> activateItem(@PathVariable String itemId) {
        return zohoItemService.activateItem(itemId);
    }

    @PUT("/items/deactivate/{itemId}")
    public APIResponse<ZohoResponse> deActivateItem(@PathVariable String itemId) {
        return zohoItemService.deActivateItem(itemId);
    }

    @DELETE("/items/delete/{itemId}")
    public APIResponse<ZohoResponse> deleteItem(@PathVariable String itemId) {
        return zohoItemService.deleteItem(itemId);
    }


    @GetMapping("/items/{itemId}")
    public APIResponse<ZohoSingleItemResponse> getItem(@PathVariable String itemId) {
        return zohoItemService.getItem(itemId);
    }

    @GetMapping("/items")
    public APIResponse<ZohoMultiItemResponse> getItems() {
        return zohoItemService.getItems();
    }
}
