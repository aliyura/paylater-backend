package com.syrol.paylater.services;
import com.syrol.paylater.entities.User;
import com.syrol.paylater.pojos.APIResponse;
import com.syrol.paylater.pojos.UserRequest;
import com.syrol.paylater.repositories.UserRepository;
import com.syrol.paylater.util.App;
import com.syrol.paylater.util.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class MessagingService implements Serializable {

    @Autowired
    private final App app;
    @Autowired
    private final Response response;
    @Autowired
    private RestTemplate rest;
    private final UserRepository userRepository;
    private String smsBaseURL="http://smshub.lubredsms.com/hub/jsonsmsapi/send";
    private String username="paylater";
    private String password="BZXcpXrD";
    private String sender="PayLaterHub";


    public APIResponse sendSMS(String recipient,String textMessage) {
        app.print("Sending SMS...");
        app.print(""+recipient);
        app.print(""+textMessage);
        try {
            HttpEntity<String> entity = new HttpEntity<String>(app.getHTTPHeaders());
            ResponseEntity<String> response = rest.exchange(smsBaseURL
                            + "?user=" + username
                            + "&pass=" + password
                            + "&sender="+sender
                            + "&message=" + textMessage
                            + "&mobile="+recipient
                            + "&flash=0"
                    , HttpMethod.POST, entity, String.class);

            app.print("is success: "+response.getStatusCode().is2xxSuccessful());
            app.print("Status: "+response.getStatusCode());
            app.print("body"+response.getBody());

            if (response.getStatusCode().is2xxSuccessful()) {
                app.print("Message Sent");
                return new APIResponse("Message Sent", true, response.getBody());
            } else {
                app.print("Failed");
                return new APIResponse("Failed to send Message", false, response.getBody());
            }
        }catch (Exception ex){
            app.print("Unable to send Message");
            ex.printStackTrace();
            return new APIResponse(ex.getMessage(), false, null);
        }
    }

    public APIResponse generateAndSendOTP(UserRequest userRequest) {
         app.print(userRequest);
        User appUser = userRepository.findByEmail(userRequest.getUsername()).orElse(userRepository.findByMobile(userRequest.getUsername()).orElse(null));
        if (appUser != null) {
            Long otp=app.generateOTP();
            appUser.setCode(otp);
            appUser.setLastModifiedDate(new Date());
            userRepository.save(appUser);
            //send SMS
            APIResponse messengerResponse= this.sendSMS(appUser.getMobile(),"Your Paylater OTP is "+otp);
             if(!messengerResponse.isSuccess())
                return response.failure("Unable to send OTP");
             else
               return response.success("OTP sent to "+appUser.getMobile());
        } else {
            return response.failure("Account not found");
        }
    }
}
