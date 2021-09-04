package com.syrol.paylater.controllers;
import com.syrol.paylater.pojos.*;
import com.syrol.paylater.pojos.remita.LiquidationRequest;
import com.syrol.paylater.pojos.zoho.ZohoContactRequest;
import com.syrol.paylater.services.MessagingService;
import com.syrol.paylater.services.LiquidationService;
import com.syrol.paylater.services.ZohoContactService;
import com.syrol.paylater.util.App;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@AllArgsConstructor
@RequestMapping(path = "api/v1/")
public class AppController {


    private final MessagingService messagingService;
    private final LiquidationService remitaService;
    private final ZohoContactService zohoContactService;
    private final App app;

    @GetMapping("/ping")
    public APIResponse<String> ping(){
        return new APIResponse<String>("success", true, "I am alive");
    }

//    @GetMapping("/test")
//    public APIResponse test(@RequestBody ZohoContactRequest request){
//        return zohoContactService.createContact(request);
//    }
//
//    @GetMapping("/test1")
//    public APIResponse<String> test(Principal principal, @RequestBody LiquidationRequest request){
//       //  remitaService.directDebit();
//       //  remitaService.sendDebitInstruction(request);
//       //  remitaService.cancelDebitInstruction(request);
//        // remitaService.initiateDirectDebit(principal,request);
//         return  null;
//    }
}
