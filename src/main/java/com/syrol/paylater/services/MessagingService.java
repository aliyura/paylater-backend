package com.syrol.paylater.services;
import com.syrol.paylater.entities.User;
import com.syrol.paylater.entities.VerificationRequest;
import com.syrol.paylater.enums.Status;
import com.syrol.paylater.pojos.APIResponse;
import com.syrol.paylater.pojos.UserCredRequest;
import com.syrol.paylater.pojos.UserRequest;
import com.syrol.paylater.pojos.UserVerificationRequest;
import com.syrol.paylater.repositories.UserRepository;
import com.syrol.paylater.repositories.VerificationRepository;
import com.syrol.paylater.util.App;
import com.syrol.paylater.util.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.Serializable;
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
    private final VerificationRepository verificationRepository;
    private String smsBaseURL = "http://smshub.lubredsms.com/hub/jsonsmsapi/send";
    @Value("${paylater.sms.sender}")
    private String sender;
    @Value("${paylater.sms.username}")
    private String username;
    @Value("${paylater.sms.password}")
    private String password;


    public APIResponse sendSMS(String recipient, String textMessage) {
        app.print("Sending SMS...");
        app.print("" + recipient);
        app.print("" + textMessage);
        try {
            HttpEntity<String> entity = new HttpEntity<String>(app.getHTTPHeaders());
            ResponseEntity<String> response = rest.exchange(smsBaseURL
                            + "?user=" + username
                            + "&pass=" + password
                            + "&sender=" + sender
                            + "&message=" + textMessage
                            + "&mobile=" + recipient
                            + "&flash=0"
                    , HttpMethod.POST, entity, String.class);

            app.print("is success: " + response.getStatusCode().is2xxSuccessful());
            app.print("Status: " + response.getStatusCode());
            app.print("body" + response.getBody());

            if (response.getStatusCode().is2xxSuccessful()) {
                app.print("Message Sent");
                return new APIResponse("Message Sent", true, response.getBody());
            } else {
                app.print("Failed");
                return new APIResponse("Failed to send Message", false, response.getBody());
            }
        } catch (Exception ex) {
            app.print("Unable to send Message");
            ex.printStackTrace();
            return new APIResponse(ex.getMessage(), false, null);
        }
    }

    public APIResponse generateAndSendOTP(UserRequest request) {
        app.print(request);
        if(request.getUsername().startsWith("0") && app.validNumber(request.getUsername())){
            request.setUsername(request.getUsername().replaceFirst("0","+234"));
        }
        User appUser = userRepository.findByEmail(request.getUsername()).orElse(userRepository.findByMobile(request.getUsername()).orElse(null));
        if (appUser != null) {
            Long otp = app.generateOTP();
            appUser.setCode(otp);
            appUser.setLastModifiedDate(new Date());
            userRepository.save(appUser);
            //send SMS
            APIResponse messengerResponse = this.sendSMS(appUser.getMobile(), "Your Paylater OTP is " + otp);
            if (!messengerResponse.isSuccess())
                return response.failure("Unable to send OTP");
            else
                return response.success("OTP sent to " + appUser.getMobile());
        } else {
            return response.failure("Account not found");
        }
    }

    public APIResponse generateAndSendOTPWithoutAuth(UserRequest request) {
        app.print(request);
        if(request.getUsername().startsWith("0") && app.validNumber(request.getUsername())){
            request.setUsername(request.getUsername().replaceFirst("0","+234"));
        }
        VerificationRequest existingVerificationRequest = verificationRepository.findByUsername(request.getUsername()).orElse(null);
        Long otp = app.generateOTP();
        if (existingVerificationRequest != null) {

            existingVerificationRequest.setVerificationCode(String.valueOf(otp));
            existingVerificationRequest.setUsername(request.getUsername());
            existingVerificationRequest.setCreatedDate(new Date());
            verificationRepository.save(existingVerificationRequest);
            //send SMS

            APIResponse messengerResponse = this.sendSMS(existingVerificationRequest.getUsername(), "Your Paylater OTP is " + otp);
            if (!messengerResponse.isSuccess())
                return response.failure("Unable to send OTP");
            else
                return response.success("OTP sent to " + existingVerificationRequest.getUsername());

        } else {
            VerificationRequest verificationRequest = new VerificationRequest();
            verificationRequest.setVerificationCode(String.valueOf(otp));
            verificationRequest.setUsername(request.getUsername());
            verificationRequest.setCreatedDate(new Date());
            verificationRepository.save(verificationRequest);
            //send SMS
            APIResponse messengerResponse = this.sendSMS(verificationRequest.getUsername(), "Your Paylater OTP is " + otp);
            if (!messengerResponse.isSuccess())
                return response.failure("Unable to send OTP");
            else
                return response.success("OTP sent to " + verificationRequest.getUsername());
        }
    }

    public APIResponse verifyOTPWithoutAuth(UserVerificationRequest verificationRequest) {
        app.print(verificationRequest);
        if(verificationRequest.getUsername().startsWith("0") && app.validNumber(verificationRequest.getUsername())){
            verificationRequest.setUsername(verificationRequest.getUsername().replaceFirst("0","+234"));
        }
        VerificationRequest existingVerificationRequest = verificationRepository.findByUsername(verificationRequest.getUsername()).orElse(null);
        if (existingVerificationRequest != null) {

            app.print("Requested OTP:");
            app.print(verificationRequest);

            app.print("Generated OTP");
            app.print(existingVerificationRequest);

            if (existingVerificationRequest.getVerificationCode().equals(verificationRequest.getOtp())) {
                verificationRepository.deleteById(existingVerificationRequest.getId());
                return response.success("OTP Verified Successfully");
            } else {
                return response.failure("Invalid OTP");
            }

        } else {
            return  response.failure("Invalid OTP or Expired");
        }
    }
}
