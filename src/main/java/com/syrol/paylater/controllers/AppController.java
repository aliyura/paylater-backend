package com.syrol.paylater.controllers;
import com.syrol.paylater.pojos.*;
import com.syrol.paylater.pojos.remita.LiquidationRequest;
import com.syrol.paylater.pojos.zoho.ZohoContactRequest;
import com.syrol.paylater.pojos.zoho.ZohoTokenResponse;
import com.syrol.paylater.services.*;
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
    private  final ZohoAuthService zohoAuthService;
    private  final EmailService emailService;
    private final App app;

    @GetMapping("/ping")
    public APIResponse<String> ping(){
        return new APIResponse<String>("success", true, "I am alive");
    }

    @GetMapping("/test")
    public ZohoTokenResponse test(){
        return zohoAuthService.authManager();
    }

    @GetMapping("/test1")
    public APIResponse<String> test1(){
       //  remitaService.directDebit();
       //  remitaService.sendDebitInstruction(request);
       //  remitaService.cancelDebitInstruction(request);
        // remitaService.initiateDirectDebit(principal,request);

        EmailMessage message= new EmailMessage();
        message.setBody("This is the email body");
        message.setRecipient("net.rabiualiyu@gmail.com");
        message.setRecipientName("Rabiu Aliyu");
        message.setSubject("Test From Paylater");

         return  emailService.sendEmail(message);
    }
}
