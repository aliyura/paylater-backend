package com.syrol.paylater.controllers;

import com.syrol.paylater.pojos.APIResponse;
import com.syrol.paylater.pojos.PaymentRequest;
import com.syrol.paylater.services.PaymentService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;

@RestController
@AllArgsConstructor
@RequestMapping(path = "api/v1/")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/payment/initialization")
    public APIResponse<String> initiatePayment(@Valid @RequestBody PaymentRequest request) {
        return paymentService.initializePayment(request);
    }

    @GetMapping("/payment/verification")
    public APIResponse<String> verifyPayment(@RequestParam String reference){
        return paymentService.verifyPayment(reference);
    }

}
