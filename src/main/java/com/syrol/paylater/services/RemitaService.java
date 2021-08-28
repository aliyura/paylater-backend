package com.syrol.paylater.services;

import com.syrol.paylater.entities.DirectDebitHistory;
import com.syrol.paylater.pojos.APIResponse;
import com.syrol.paylater.pojos.DirectDebitRequest;
import com.syrol.paylater.pojos.PaymentRequest;
import com.syrol.paylater.pojos.PaymentResponse;
import com.syrol.paylater.retrofitservices.PaymentServiceInterface;
import com.syrol.paylater.retrofitservices.RemitaServiceInterface;
import com.syrol.paylater.util.App;
import com.syrol.paylater.util.UnsafeOkHttpClient;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import javax.annotation.PostConstruct;

@RefreshScope
@Service
@RequiredArgsConstructor
public class RemitaService {

    private final App app;
    private RemitaServiceInterface remitaServiceInterface;
    private String baseURL="https://remitademo.net";
    private String merchantId="";
    private OkHttpClient okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();

    @PostConstruct
    public void init() {
        Retrofit retrofit = new Retrofit.Builder().client(okHttpClient).addConverterFactory(GsonConverterFactory.create())
                .baseUrl(baseURL)
                .build();
        remitaServiceInterface= retrofit.create(RemitaServiceInterface.class);
    }

    public APIResponse directDebit(DirectDebitRequest request){
        try {
            request.setMerchantId(merchantId);
            app.print("#########Direct Debit Request");
            app.print(request);

            Response<Object> response = remitaServiceInterface.directDebit(request).execute();
            app.print("#########Response:");
            app.print(response);
            if (response.isSuccessful()){
                return new APIResponse<>("Request Successful", true, response.body());
            } else {
                return new APIResponse<>("Request Failed "+response.code(), false, response);
            }
        }catch (Exception ex){
            ex.printStackTrace();
            return new APIResponse<>(ex.getMessage(), false, null);
        }
    }
}
