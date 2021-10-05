package com.syrol.paylater.controllers;

import com.syrol.paylater.entities.DirectDebit;
import com.syrol.paylater.enums.Status;
import com.syrol.paylater.pojos.*;
import com.syrol.paylater.pojos.mono.MonoDirectDebitRequest;
import com.syrol.paylater.services.MonoLiquidationService;
import com.syrol.paylater.util.Response;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@AllArgsConstructor
@RequestMapping(path = "api/v1/")
public class LiquidationController {

    private final MonoLiquidationService monoLiquidationService;
    private  final Response response;

    @PostMapping("/liquidation/initiate/{reference}")
    public APIResponse<String> initiateDirectDebit(Principal principal, @PathVariable String reference, @RequestBody MonoDirectDebitRequest request){
        return monoLiquidationService.initiateDirectDebit(principal,reference,request);
    }

    @GetMapping("/liquidation/status/{debitReference}")
    public APIResponse<String> checkDirectDebitStatus(Principal principal, @PathVariable String debitReference){
        return monoLiquidationService.checkDirectDebitStatus(principal,debitReference);
    }

    @GetMapping("/liquidation/history/get_all")
    public APIResponse<Page<DirectDebit>> getDirectDebitHistory(@RequestParam int page, @RequestParam int size) {
        return monoLiquidationService.directDebitHistory(PageRequest.of(page,size));
    }

    @GetMapping("/liquidation/history/get_by_status")
    public APIResponse<Page<DirectDebit>> getDirectDebitHistoryByStatus(@RequestParam Status status, @RequestParam int page, @RequestParam int size) {
        return monoLiquidationService.directDebitHistoryByStatus(PageRequest.of(page,size),status);
    }
}
