package com.syrol.paylater.controllers;

import com.syrol.paylater.pojos.*;
import com.syrol.paylater.pojos.remita.*;
import com.syrol.paylater.services.RemitaService;
import com.syrol.paylater.util.Response;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@AllArgsConstructor
@RequestMapping(path = "api/v1/")
public class DirectDebitController {

    private final RemitaService remitaService;
    private  final Response response;

    @PostMapping("/direct-debit/initiate")
    public APIResponse<String> initiateDirectDebit(Principal principal, @RequestBody DirectDebitRequest request){
        return remitaService.initiateDirectDebit(principal,request);
    }

    @PostMapping("/direct-debit/run")
    public APIResponse<String> sendDirectDebitInstruction(Principal principal, @RequestBody DirectDebitInstructionRequest request){
        return remitaService.sendDebitInstruction(principal,request);
    }

    @PostMapping("/direct-debit/cancel")
    public APIResponse<String> cancelDirectDebitInstruction(Principal principal, @RequestBody DirectDebitCancelInstructionRequest request){
        return remitaService.cancelDebitInstruction(principal,request);
    }

    @GetMapping("/direct-debit/status")
    public APIResponse<String> checkDirectDebitStatus(Principal principal, @RequestParam String mandateId, @RequestParam String requestId){
        if(mandateId==null)
            return  response.failure("Mandate Id Required <mandateId>!");
        if(requestId==null)
            return  response.failure("Request Id Required <requestId>!");
        else {
            DirectDebitStatusRequest request = new DirectDebitStatusRequest();
            request.setRequestId(requestId);
            request.setMandateId(mandateId);
            return remitaService.checkDirectDebitStatus(principal, request);
        }
    }
    @GetMapping("/direct-debit/history")
    public APIResponse<String> getDirectDebitHistory(Principal principal, @RequestParam String mandateId, @RequestParam String requestId){
        if(mandateId==null)
            return  response.failure("Mandate Id Required <mandateId>!");
        if(requestId==null)
            return  response.failure("Request Id Required <requestId>!");
        else {
            DirectDebitHistoryRequest request = new DirectDebitHistoryRequest();
            request.setRequestId(requestId);
            request.setMandateId(mandateId);
            return remitaService.directDebitHistory(principal, request);
        }
    }
}
