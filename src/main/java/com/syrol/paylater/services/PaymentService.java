package com.syrol.paylater.services;

import com.syrol.paylater.entities.Payment;
import com.syrol.paylater.entities.User;
import com.syrol.paylater.enums.Status;
import com.syrol.paylater.pojos.APIResponse;
import com.syrol.paylater.pojos.paystack.PaymentInitializeRequest;
import com.syrol.paylater.pojos.paystack.PaymentInitializeResponse;
import com.syrol.paylater.pojos.paystack.PaymentVerificationResponse;
import com.syrol.paylater.pojos.paystack.PaymentVerificationResponseData;
import com.syrol.paylater.repositories.PaymentRepository;
import com.syrol.paylater.retrofitservices.PaymentServiceInterface;
import com.syrol.paylater.util.App;
import com.syrol.paylater.util.AuthDetails;
import com.syrol.paylater.util.UnsafeOkHttpClient;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import  org.springframework.beans.factory.annotation.Value;
import javax.annotation.PostConstruct;
import java.security.Principal;
import java.util.Date;
import java.util.List;

@RefreshScope
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final App app;
    private final AuthDetails authDetails;
    private final com.syrol.paylater.util.Response apiResponse;
    private final PaymentRepository paymentRepository;
    private PaymentServiceInterface integrationService;
    private String baseURL = "https://api.paystack.co";
    @Value("${spring.paylater.paystack.token}")
    private String secretKey;
    private OkHttpClient okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();

    @PostConstruct
    public void init() {
        Retrofit retrofit = new Retrofit.Builder().client(okHttpClient).addConverterFactory(GsonConverterFactory.create())
                .baseUrl(baseURL)
                .build();
        integrationService = retrofit.create(PaymentServiceInterface.class);
    }

    public APIResponse initializePayment(Principal principal, PaymentInitializeRequest request, String orderReference) {
        try {

            if (orderReference == null)
                return apiResponse.failure("Order Reference Required!");

            User user = authDetails.getAuthorizedUser(principal);
            app.print("#########Initialize Payment Request");
            app.print(request);
            String authorization = String.format("Bearer %s", secretKey);
            app.print(authorization);
            Response<PaymentInitializeResponse> response = integrationService.initializePayment(authorization, request).execute();
            app.print("#########Response:");
            app.print(response);
            app.print(response.body().getData());
            app.print(response.message());

            if (response.isSuccessful()) {
                //Initialize Payment
                Payment payment = new Payment();
                payment.setAmount(request.getAmount());
                payment.setEmail(request.getEmail());
                payment.setReference(request.getReference());
                payment.setContactId(user.getContactId());
                payment.setUuid(user.getUuid());
                payment.setOrderReference(orderReference);
                payment.setName(user.getName());
                payment.setStatus(Status.PP);
                payment.setCreatedDate(new Date());
                app.print(payment);
                paymentRepository.save(payment);

                return new APIResponse<>("Request Successful", true, response.body().getData());
            } else {
                return new APIResponse<>("Request Failed " + response.code(), false, response);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return new APIResponse<>(ex.getMessage(), false, null);
        }
    }


    public APIResponse verifyPayment(Principal principal, String reference) {
        try {
            if (reference == null)
                return apiResponse.failure("Payment Reference Required!");

            app.print("#########Verify Payment Request");
            app.print(reference);
            User user = authDetails.getAuthorizedUser(principal);
            Payment initialPayment = paymentRepository.findByReference(reference).orElse(null);
            if (initialPayment != null) {
                String authorization = String.format("Bearer %s", secretKey);
                Response<PaymentVerificationResponse> response = integrationService.verifyPayment(authorization, reference).execute();
                app.print("#########Response:");
                app.print(response);
                if (response.isSuccessful()) {
                    PaymentVerificationResponseData responseData = response.body().getData();
                    if (responseData.getStatus().equalsIgnoreCase("success")) {

                        //Payment request
                        initialPayment.setPaymentId(responseData.getId());
                        initialPayment.setStatus(Status.AC);
                        initialPayment.setChannel(responseData.getChannel());
                        initialPayment.setPaidAt(responseData.getPaid_at());
                        initialPayment.setCurrency(responseData.getCurrency());
                        initialPayment.setGateway_response(responseData.getGateway_response());
                        initialPayment.setIp_address(responseData.getIp_address());
                        initialPayment.setDomain(responseData.getDomain());
                        initialPayment.setLastModifiedDate(new Date());
                        paymentRepository.save(initialPayment);

                        return apiResponse.success(initialPayment);
                    } else {
                        return apiResponse.fail(responseData);
                    }
                } else {
                    return apiResponse.fail(response);
                }

            } else {
                return apiResponse.failure("Invalid Payment Reference!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return apiResponse.failure(ex.getMessage());
        }
    }
    public APIResponse<Payment> getPayment(Principal principal, String reference) {
        Payment payment=paymentRepository.findByReference(reference).orElse(null);
        if(payment!=null){
            return  apiResponse.success(payment);
        }else{
            return apiResponse.failure("Invalid Payment Reference");
        }
    }

    public APIResponse<List<Payment>> getMyPayments(Principal principal) {
        User user =authDetails.getAuthorizedUser(principal);
        List<Payment> payment=paymentRepository.findByUuid(user.getUuid()).orElse(null);
        if(payment!=null){
            return  apiResponse.success(payment);
        }else{
            return apiResponse.failure("No Payment Found");
        }
    }

    public APIResponse<Page<Payment>> getPayments(Principal principal, Pageable pageable) {
        Page<Payment> paymentPage= paymentRepository.findAll(pageable);
        if(paymentPage!=null){
            return  apiResponse.success(paymentPage);
        }else{
            return apiResponse.failure("No Payment Available");
        }
    }
    public APIResponse<Payment> getActivePayments(Principal principal,  Pageable pageable) {
        Page<Payment> paymentPage= paymentRepository.findByStatus(Status.AC,pageable).orElse(null);
        if(paymentPage!=null){
            return  apiResponse.success(paymentPage);
        }else{
            return apiResponse.failure("No Active Payment Available");
        }
    }

    public APIResponse<Payment> getCanceledPayments(Principal principal,  Pageable pageable) {
        Page<Payment> paymentPage= paymentRepository.findByStatus(Status.CA,pageable).orElse(null);
        if(paymentPage!=null){
            return  apiResponse.success(paymentPage);
        }else{
            return apiResponse.failure("No Canceled Payment Available");
        }
    }

    public APIResponse<Payment> getPendingPayments(Principal principal,Pageable pageable) {
        Page<Payment> paymentPage= paymentRepository.findByStatus(Status.PP, pageable).orElse(null);
        if(paymentPage!=null){
            return  apiResponse.success(paymentPage);
        }else{
            return apiResponse.failure("No Pending Payment Available");
        }
    }
}
