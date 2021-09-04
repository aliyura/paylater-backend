package com.syrol.paylater.controllers;

import com.syrol.paylater.pojos.*;
import com.syrol.paylater.pojos.remita.*;
import com.syrol.paylater.services.LiquidationService;
import com.syrol.paylater.util.Response;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@AllArgsConstructor
@RequestMapping(path = "api/v1/")
public class LiquidationController {

    private final LiquidationService remitaService;
    private  final Response response;

    @PostMapping("/liquidation/initiate/{reference}")
    public APIResponse<String> initiateDirectDebit(Principal principal, @PathVariable String reference, @RequestBody LiquidationRequest request){
        return remitaService.initiateDirectDebit(principal,reference,request);
    }

    @PostMapping("/liquidation/run/{orderReference}")
    public APIResponse<String> sendDirectDebitInstruction(Principal principal, @PathVariable String orderReference, @RequestBody LiquidationInstructionRequest request){
        return remitaService.sendDebitInstruction(principal,orderReference,request);
    }

    @PostMapping("/liquidation/cancel")
    public APIResponse<String> cancelDirectDebitInstruction(Principal principal, @RequestBody LiquidationCancelInstructionRequest request){
        return remitaService.cancelDebitInstruction(principal,request);
    }

    @GetMapping("/liquidation/status/{orderReference}")
    public APIResponse<String> checkDirectDebitStatus(Principal principal, @PathVariable String orderReference, @RequestParam String mandateId, @RequestParam String requestId){
        if(mandateId==null)
            return  response.failure("Mandate Id Required <mandateId>!");
        if(requestId==null)
            return  response.failure("Request Id Required <requestId>!");
        else {
            LiquidationStatusRequest request = new LiquidationStatusRequest();
            request.setRequestId(requestId);
            request.setMandateId(mandateId);
            return remitaService.checkDirectDebitStatus(principal,orderReference, request);
        }
    }
    @GetMapping("/liquidation/history")
    public APIResponse<String> getDirectDebitHistory(Principal principal, @RequestParam String mandateId, @RequestParam String requestId){
        if(mandateId==null)
            return  response.failure("Mandate Id Required <mandateId>!");
        if(requestId==null)
            return  response.failure("Request Id Required <requestId>!");
        else {
            LiquidationHistoryRequest request = new LiquidationHistoryRequest();
            request.setRequestId(requestId);
            request.setMandateId(mandateId);
            return remitaService.directDebitHistory(principal, request);
        }
    }
}
