package com.syrol.paylater.services;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.syrol.paylater.pojos.zoho.*;
import com.syrol.paylater.retrofitservices.ZohoAuthServiceInterface;
import com.syrol.paylater.util.App;
import com.syrol.paylater.util.UnsafeOkHttpClient;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import javax.annotation.PostConstruct;

@Service
@RequiredArgsConstructor
public class ZohoAuthService {


    private final App app;
    private final com.syrol.paylater.util.Response apiResponse;
    private ZohoAuthServiceInterface zohoAuthServiceInterface;
    private OkHttpClient okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();
    @Value("${spring.zoho.authBaseURL}")
    private String baseAuthURL;
    @Value("${spring.zoho.clientId}")
    private String clientId;
    @Value("${spring.zoho.clientSecret}")
    private String clientSecret;
    @Value("${spring.zoho.grantType}")
    private String grantType;
    @Value("${spring.zoho.redirectURL}")
    private String redirectURL;
    @Value("${spring.zoho.refreshToken}")
    private String refreshToken;


    @PostConstruct
    public void init() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder().client(okHttpClient).addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(baseAuthURL)
                .build();
        zohoAuthServiceInterface = retrofit.create(ZohoAuthServiceInterface.class);
    }

    public ZohoTokenResponse authManager() {
        try {
            app.print("#########Generating Zoho access token");
            Response<ZohoTokenResponse> tokenResponseResponse = zohoAuthServiceInterface.generateAccessToKen(refreshToken, clientId, clientSecret, redirectURL, grantType).execute();
            if(tokenResponseResponse.isSuccessful()){
                app.print("Response:");
                app.print(tokenResponseResponse.body());
                return  tokenResponseResponse.body();
            }
            app.print("Response:");
            app.print(tokenResponseResponse.headers());
            app.print(tokenResponseResponse.code());
            return tokenResponseResponse.body();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}