package com.syrol.paylater.controllers;

import com.syrol.paylater.pojos.APIResponse;
import com.syrol.paylater.services.MessagingService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
@RestController
@AllArgsConstructor
@RequestMapping(path = "api/v1/")
public class AppController {


    private final MessagingService messagingService;

    @GetMapping("/ping")
    public APIResponse<String> ping(){
        return new APIResponse<String>("success", true, "I am alive");
    }

    @GetMapping("/test")
    public APIResponse<String> test(){
        return messagingService.sendSMS("2348064160204","Hello World");
    }


}
