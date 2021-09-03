package com.syrol.paylater.services;

import com.syrol.paylater.pojos.APIResponse;
import com.syrol.paylater.pojos.paystack.PaymentRequest;
import com.syrol.paylater.pojos.paystack.PaymentResponse;
import com.syrol.paylater.retrofitservices.PaymentServiceInterface;
import com.syrol.paylater.util.App;
import com.syrol.paylater.util.UnsafeOkHttpClient;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import  org.springframework.beans.factory.annotation.Value;
import javax.annotation.PostConstruct;

@RefreshScope
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final App app;
    private PaymentServiceInterface integrationService;
    private String baseURL="https://api.paystack.co";
    @Value("${spring.paylater.paystack.token}")
    private String secretKey;
    private OkHttpClient okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();

    @PostConstruct
    public void init() {
        Retrofit retrofit = new Retrofit.Builder().client(okHttpClient).addConverterFactory(GsonConverterFactory.create())
                .baseUrl(baseURL)
                .build();
        integrationService= retrofit.create(PaymentServiceInterface.class);
    }

    public APIResponse initializePayment(PaymentRequest request){
        try {
            app.print("#########Initialize Payment Request");
            app.print(request);
            String authorization = String.format("Bearer %s",secretKey);
            Response<PaymentResponse> response = integrationService.initializePayment(authorization, request).execute();
            app.print("#########Response:");
            app.print(response);

            if (response.isSuccessful()){
                return new APIResponse<>("Request Successful", true, response.body().getData());
            } else {
                return new APIResponse<>("Request Failed "+response.code(), false, response);
            }
        }catch (Exception ex){
            ex.printStackTrace();
            return new APIResponse<>(ex.getMessage(), false, null);
        }
    }


    public APIResponse verifyPayment(String reference){
        try {
            app.print("#########Verify Payment Request");
            app.print(reference);
            String authorization = String.format("Bearer %s",secretKey);
            Response<PaymentResponse> response = integrationService.verifyPayment(authorization, reference).execute();
            app.print("#########Response:");
            app.print(response);

            if (response.isSuccessful()){
                return new APIResponse<>("Request Successful", true, response.body().getData());
            } else {
                return new APIResponse<>("Request Failed "+response.code(), false, response);
            }
        }catch (Exception ex){
            ex.printStackTrace();
            return new APIResponse<>(ex.getMessage(), false, null);
        }
    }
}
