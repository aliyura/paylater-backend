package com.syrol.paylater.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.JsonAdapter;
import com.syrol.paylater.pojos.*;
import com.syrol.paylater.retrofitservices.ZohoServiceInterface;
import com.syrol.paylater.util.App;
import com.syrol.paylater.util.UnsafeOkHttpClient;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import org.springframework.stereotype.Service;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import javax.annotation.PostConstruct;

@Service
@RequiredArgsConstructor
public class ZohoService {

    private final App app;
    private final com.syrol.paylater.util.Response apiResponse;
    private ZohoServiceInterface zohoServiceInterface;
    private String baseURL="https://books.zoho.com";
    private String organization="755989192";
    private String clientId="1000.QDIKEL9A9NCYDDV5WE7BWQUP4VF6ZQ";
    private String clientSecret="2fa4567ab1f9423745f4d8ae3af44b892a0e47d7cd";
    private String grantType="authorization_code" ;
    private String scope="ZohoBooks.fullaccess.all";
    private String accessType="offline";
    private String redirectURL="http://www.zoho.com/books";
    private String code="1000.4915fba0a179e65234571f6c38490db4.50f1c49a382dcaf671f4d51f40d3daa4";
    private  String accessToken="1000.7d4c8f5bd82e3588543fa03e259d3255.f2b8d419cfa3279f2d6c135df708ec90";
    private   OkHttpClient okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();



    @PostConstruct
    public void init() {
        Retrofit retrofit = new Retrofit.Builder().client(okHttpClient).addConverterFactory(GsonConverterFactory.create())
                .baseUrl(baseURL)
                .build();
        zohoServiceInterface= retrofit.create(ZohoServiceInterface.class);
    }

    public ZohoTokenResponse getAccessToken(){
        try {
            app.print("#########Generating Zoho access token");
            Response<ZohoTokenResponse> tokenResponseResponse = zohoServiceInterface.generateToken(code, clientId, clientSecret, redirectURL, grantType, scope).execute();
            if(tokenResponseResponse.isSuccessful()){
                app.print("Response:");
                app.print(tokenResponseResponse.body());
                return  tokenResponseResponse.body();
            }
            app.print("Response:");
            app.print(tokenResponseResponse.headers());
            app.print(tokenResponseResponse.code());
        return null;
      }catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public APIResponse createUser(ZohoCreateUserRequest request) {
        try {
            app.print("#########Zoho Create User Request");
            app.print(request);
            String authorization = String.format("Bearer %s",accessToken);
            Response<ZohoResponse> createUserResponse = zohoServiceInterface.createUser(authorization,request,organization).execute();
            app.print(createUserResponse);
            app.print(createUserResponse.code());
            if(createUserResponse.isSuccessful()){
                app.print("Response:");
                app.print(createUserResponse.body());
                return  apiResponse.success(createUserResponse);
            }
            else{
                return apiResponse.failure(createUserResponse.message());
            }

        }catch (Exception ex){
            ex.printStackTrace();
            return apiResponse.failure(ex.getMessage());
        }
    }


//
//    public APIResponse updateUser(String userId,ZohoCreateUserRequest request) {
//
//        HttpHeaders headers = app.getHTTPHeaders();
//        headers.add("Authorization", token);
//
//        app.print("Zoho Create User Request:" + request);
//
//        HttpEntity<String> entity = new HttpEntity<String>(app.getHTTPHeaders());
//        ResponseEntity<String> response = rest.exchange(baseURL + "/users/"+userId+"?" +
//                "organization_id=" + organization +
//                "&name=" + request.getName() +
//                "&email" + request.getEmail() +
//                "&role_id=" + request.getRoleId() +
//                "&cost_rate=" + request.getCostRate(), HttpMethod.POST, entity, String.class);
//
//        app.print("Response:" + response.getBody());
//
//        if (response.getStatusCode().is2xxSuccessful()) {
//            return new APIResponse<>("Request Successful", true, response.getBody());
//        } else {
//            return new APIResponse<>("Request Failed", false, response.getBody());
//        }
//    }
//
//    public APIResponse getUsers() {
//
//        HttpHeaders headers = app.getHTTPHeaders();
//        headers.add("Authorization", token);
//
//        app.print("Zoho List Users Request");
//
//        HttpEntity<String> entity = new HttpEntity<String>(app.getHTTPHeaders());
//        ResponseEntity<String> response = rest.exchange(baseURL + "/users?organization_id=" + organization, HttpMethod.GET, entity, String.class);
//        app.print("Response:" + response.getBody());
//
//        if (response.getStatusCode().is2xxSuccessful()) {
//            return new APIResponse<>("Request Successful", true, response.getBody());
//        } else {
//            return new APIResponse<>("Request Failed", false, response.getBody());
//        }
//    }
//
//    public APIResponse getUserById(String userId) {
//
//        HttpHeaders headers = app.getHTTPHeaders();
//        headers.add("Authorization", token);
//
//        app.print("Zoho Get User Request:"+userId);
//
//        HttpEntity<String> entity = new HttpEntity<String>(app.getHTTPHeaders());
//        ResponseEntity<String> response = rest.exchange(baseURL + "/users/"+userId+"?organization_id=" + organization, HttpMethod.GET, entity, String.class);
//        app.print("Response:" + response.getBody());
//
//        if (response.getStatusCode().is2xxSuccessful()) {
//            return new APIResponse<>("Request Successful", true, response.getBody());
//        } else {
//            return new APIResponse<>("Request Failed", false, response.getBody());
//        }
//    }
}
