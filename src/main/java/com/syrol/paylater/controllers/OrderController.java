package com.syrol.paylater.controllers;
import com.syrol.paylater.entities.Order;
import com.syrol.paylater.pojos.APIResponse;
import com.syrol.paylater.pojos.OrderActivationRequest;
import com.syrol.paylater.pojos.OrderCancellationRequest;
import com.syrol.paylater.pojos.zoho.ZohoOrderRequest;
import com.syrol.paylater.services.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@RestController
@AllArgsConstructor
@RequestMapping(path = "api/v1/")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/order/initiate")
    public APIResponse<Order> initiateOrder(Principal principal, @RequestBody ZohoOrderRequest request) {
        return orderService.initiateOrder(principal, request);
    }

    @PostMapping("/order/activate/{orderReference}")
    public APIResponse<Order> activateOrder(Principal principal, @PathVariable String orderReference, @RequestBody OrderActivationRequest request) {
        return orderService.updateOrder(principal, orderReference, request);
    }

    @PostMapping("/order/cancel/{orderReference}")
    public APIResponse<Order> cancelOrder(Principal principal, @PathVariable String orderReference, @RequestBody OrderCancellationRequest request) {
        return orderService.cancelOrder(principal,orderReference,request);
    }

    @GetMapping("/order/get/{orderReference}")
    public APIResponse<Order> getOrder(Principal principal, @PathVariable String orderReference) {
        return orderService.getOrder(principal,orderReference);
    }
    @GetMapping("/order/get_my_orders")
    public APIResponse<Order> getMyOrders(Principal principal) {
        return orderService.getMyOrders(principal);
    }

    @GetMapping("/order/get_orders")
    public APIResponse<Order> getOrders(Principal principal, @RequestParam int page, @RequestParam int size) {
        return orderService.getOrders(principal,PageRequest.of(page,size,Sort.by("id").descending()));
    }
    @GetMapping("/order/get_order_items/{orderId}")
    public APIResponse<Order> getOrders(Principal principal, @PathVariable Long orderId) {
        return orderService.getOrderItems(principal,orderId);
    }

    @GetMapping("/order/get_active_orders")
    public APIResponse<Order> getActiveOrders(Principal principal, @RequestParam int page, @RequestParam int size) {
        return orderService.getActiveOrders(principal, PageRequest.of(page,size,Sort.by("id").descending()));
    }

    @GetMapping("/order/get_canceled_orders")
    public APIResponse<Order> getCanceledOrders(Principal principal, @RequestParam int page, @RequestParam int size) {
        return orderService.getCanceledOrders(principal, PageRequest.of(page,size,Sort.by("id").descending()));
    }

    @GetMapping("/order/get_paylater_orders")
    public APIResponse<Order> getPaylaterOrders(Principal principal, @RequestParam int page, @RequestParam int size) {
        return orderService.getPaylaterOrders(principal,PageRequest.of(page,size,Sort.by("id").descending()));
    }

    @GetMapping("/order/get_pod_orders")
    public APIResponse<Order> getPodOrders(Principal principal, @RequestParam int page, @RequestParam int size) {
        return orderService.getPodOrders(principal, PageRequest.of(page,size,Sort.by("id").descending()));
    }

    @GetMapping("/order/get_paynow_orders")
    public APIResponse<Order> getPaynowOrders(Principal principal, @RequestParam int page, @RequestParam int size) {
        return orderService.getPaynowOrders(principal,PageRequest.of(page,size,Sort.by("id").descending()));
    }
}