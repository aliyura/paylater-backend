package com.syrol.paylater.services;
import com.syrol.paylater.entities.User;
import com.syrol.paylater.entities.VerificationRequest;
import com.syrol.paylater.enums.Status;
import com.syrol.paylater.pojos.*;
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
            String path=smsBaseURL
                    + "?user=" + username
                    + "&pass=" + password
                    + "&sender=" + sender
                    + "&message=" + textMessage
                    + "&mobile=" + recipient
                    + "&flash=0";
            app.print(path);
            HttpEntity<String> entity = new HttpEntity<String>(app.getHTTPHeaders());
            ResponseEntity<String> response = rest.exchange(path
                    , HttpMethod.GET, entity, String.class);

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
        VerificationRequest existingVerificationRequest = verificationRepository.findByUsername(appUser.getEmail()).orElse(null);
        Long otp = app.generateOTP();
        if (existingVerificationRequest != null) {

            existingVerificationRequest.setVerificationCode(otp);
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
            verificationRequest.setVerificationCode(otp);
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

    public APIResponse generateAndSendOTPWithoutAuth(VerifyPhoneNumberRequest request) {
        app.print(request);
        if(request.getMobileNumber().startsWith("0") && app.validNumber(request.getMobileNumber())){
            request.setMobileNumber(request.getMobileNumber().replaceFirst("0","+234"));
        }
        VerificationRequest existingVerificationRequest = verificationRepository.findByUsername(request.getMobileNumber()).orElse(null);
        Long otp = app.generateOTP();
        if (existingVerificationRequest != null) {

            existingVerificationRequest.setVerificationCode(otp);
            existingVerificationRequest.setUsername(request.getMobileNumber());
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
            verificationRequest.setVerificationCode(otp);
            verificationRequest.setUsername(request.getMobileNumber());
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
        if(verificationRequest.getMobileNumber().startsWith("0") && app.validNumber(verificationRequest.getMobileNumber())){
            verificationRequest.setMobileNumber(verificationRequest.getMobileNumber().replaceFirst("0","+234"));
        }
        VerificationRequest existingVerificationRequest = verificationRepository.findByUsername(verificationRequest.getMobileNumber()).orElse(null);
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
