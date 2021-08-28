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
import retrofit2.converter.moshi.MoshiConverterFactory;
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
    private   OkHttpClient okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();



    @PostConstruct
    public void init() {
        Retrofit retrofit = new Retrofit.Builder().client(okHttpClient).addConverterFactory(GsonConverterFactory.create())
                .baseUrl(baseURL)
                .build();
        zohoServiceInterface= retrofit.create(ZohoServiceInterface.class);
    }

    public Object getAccessToken(){
        try {
            app.print("#########Generating Zoho access token");
            Response<Object> tokenResponseResponse = zohoServiceInterface.generateToken(code, clientId, clientSecret, redirectURL, grantType, scope).execute();
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

            ZohoRequest apiRequest = new ZohoRequest();
            apiRequest.setJSONString(app.toString(request));
            app.print("#########Zoho Create User Request");
            app.print(apiRequest);

           this.getAccessToken();

           return  null;


//            if(accessTokenResponse!=null) {
//
//                String authorization = String.format("Bearer %s", accessTokenResponse.getAccess_token());
//                Response<Object> response = zohoServiceInterface.createUser(authorization, apiRequest, organization).execute();
//                app.print("#########Response:");
//
//                if (response.isSuccessful()) {
//                    return  apiResponse.success(response);
//                } else {
//                    return apiResponse.failure("Failed to create user: "+response.message());
//                }
//            }else{
//                return apiResponse.failure("Zoho Authentication Failed");
//            }
        }catch (Exception ex){
            ex.printStackTrace();
            return new APIResponse<>(ex.getMessage(), false, null);
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
