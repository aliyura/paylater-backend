package com.syrol.paylater.controllers;
import com.syrol.paylater.entities.Payment;
import com.syrol.paylater.pojos.APIResponse;
import com.syrol.paylater.services.PaymentService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "api/v1/")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/payment/verify/{reference}")
    public APIResponse<Payment> verifyPayment(Principal principal, @PathVariable String reference) {
        return paymentService.verifyPayment(principal, reference);
    }

    @GetMapping("/payment/get/{reference}")
    public APIResponse<Payment> getPayment(Principal principal, @PathVariable String reference) {
        return paymentService.getPayment(principal, reference);
    }

    @GetMapping("/payment/get_my_payments")
    public APIResponse<List<Payment>> getMyPayments(Principal principal) {
        return paymentService.getMyPayments(principal);
    }

    @GetMapping("/payment/get_payments")
    public APIResponse<Page<Payment>> getPayments(Principal principal, @RequestParam int page, @RequestParam int size) {
        return paymentService.getPayments(principal, PageRequest.of(page, size, Sort.by("id").descending()));
    }

    @GetMapping("/payment/get_active_payments")
    public APIResponse<Payment> getActivePayments(Principal principal, @RequestParam int page, @RequestParam int size) {
        return paymentService.getActivePayments(principal, PageRequest.of(page, size, Sort.by("id").descending()));
    }

    @GetMapping("/payment/get_canceled_payments")
    public APIResponse<Payment> getCanceledPayments(Principal principal, @RequestParam int page, @RequestParam int size) {
        return paymentService.getCanceledPayments(principal, PageRequest.of(page, size, Sort.by("id").descending()));
    }

    @GetMapping("/payment/get_pending_payments")
    public APIResponse<Payment> getPendingPayments(Principal principal, @RequestParam int page, @RequestParam int size) {
        return paymentService.getPendingPayments(principal, PageRequest.of(page, size, Sort.by("id").descending()));
    }
}