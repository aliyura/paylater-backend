package com.syrol.paylater.services;

import com.syrol.paylater.pojos.*;
import com.syrol.paylater.retrofitservices.CRCCreditBureauServiceInterface;
import com.syrol.paylater.retrofitservices.ZohoServiceInterface;
import com.syrol.paylater.util.App;
import com.syrol.paylater.util.UnsafeOkHttpClient;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import javax.annotation.PostConstruct;

@Service
@RequiredArgsConstructor
public class ZohoService {

    private final App app;
    private ZohoServiceInterface zohoServiceInterface;
    private String baseURL="https://books.zoho.com";
    private String organization="755989192";
    private String secretKey="c8f9ab3de03272ea44205d27ebd6985f82fa22c81d";
    OkHttpClient okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();

    @PostConstruct
    public void init() {
        Retrofit retrofit = new Retrofit.Builder().client(okHttpClient).addConverterFactory(GsonConverterFactory.create())
                .baseUrl(baseURL)
                .build();
        zohoServiceInterface= retrofit.create(ZohoServiceInterface.class);
    }

    public APIResponse createUser(ZohoCreateUserRequest request) {
        try {

            ZohoRequest apiRequest = new ZohoRequest();
            apiRequest.setJSONString(app.toString(request));
            app.print("#########Zoho Create User Request");
            app.print(apiRequest);

            String authorization = String.format("Bearer %s",secretKey);
            Response<Object> response = zohoServiceInterface.createUser(authorization, apiRequest,organization).execute();
            app.print("#########Response:");

            if (response.isSuccessful()){
                return new APIResponse<>("Request Successful", true, response);
            } else {
                return new APIResponse<>("Request Failed", false, response);
            }
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
