package com.syrol.paylater.controllers;
import com.syrol.paylater.pojos.APIResponse;
import com.syrol.paylater.pojos.ZohoCreateUserRequest;
import com.syrol.paylater.services.MessagingService;
import com.syrol.paylater.services.ZohoService;
import com.syrol.paylater.util.App;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
@RestController
@AllArgsConstructor
@RequestMapping(path = "api/v1/")
public class AppController {


    private final MessagingService messagingService;
    private final ZohoService zohoService;
    private final App app;

    @GetMapping("/ping")
    public APIResponse<String> ping(){
        return new APIResponse<String>("success", true, "I am alive");
    }

    @GetMapping("/test")
    public APIResponse<String> test(@RequestBody ZohoCreateUserRequest request){
        return zohoService.createUser(request);
    }


}
