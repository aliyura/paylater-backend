package com.syrol.paylater.controllers;
import com.syrol.paylater.pojos.*;
import com.syrol.paylater.services.MessagingService;
import com.syrol.paylater.services.RemitaService;
import com.syrol.paylater.services.ZohoService;
import com.syrol.paylater.util.App;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@AllArgsConstructor
@RequestMapping(path = "api/v1/")
public class AppController {


    private final MessagingService messagingService;
    private final RemitaService remitaService;
    private final ZohoService zohoService;
    private final App app;

    @GetMapping("/ping")
    public APIResponse<String> ping(){
        return new APIResponse<String>("success", true, "I am alive");
    }

    @GetMapping("/test")
    public APIResponse test(@RequestBody ZohoCreateUserRequest request){
        return zohoService.createUser(request);
    }

    @GetMapping("/test1")
    public APIResponse<String> test(Principal principal, @RequestBody DirectDebitRequest request){
       //  remitaService.directDebit();
       //  remitaService.sendDebitInstruction(request);
       //  remitaService.cancelDebitInstruction(request);
        return remitaService.initiateDirectDebit(principal,request);
    }

}
