package com.syrol.paylater.services;

import com.syrol.paylater.pojos.*;
import com.syrol.paylater.retrofitservices.CRCCreditBureauServiceInterface;
import com.syrol.paylater.util.App;
import com.syrol.paylater.util.UnsafeOkHttpClient;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import javax.annotation.PostConstruct;

@RefreshScope
@Service
@RequiredArgsConstructor
public class CRCCreditBureauService {

    private final App app;
    private CRCCreditBureauServiceInterface crcCreditBureauServiceInterface;
    private String baseURL="https://webserver.creditreferencenigeria.net";
    private String  username="221sbsupp";
    private String password="pass@1234";
    OkHttpClient okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();

    @PostConstruct
    public void init() {
        Retrofit retrofit = new Retrofit.Builder().client(okHttpClient).addConverterFactory(GsonConverterFactory.create())
                .baseUrl(baseURL)
                .build();
        crcCreditBureauServiceInterface= retrofit.create(CRCCreditBureauServiceInterface.class);
    }

    public APIResponse searchBVN(CRCBVNSearchRequest bvnRequest){
        try {

            CRCCreditRequest request = new CRCCreditRequest();
            request.setRequest("{'@REQUEST_ID': '1','REQUEST_PARAMETERS': {   'REPORT_PARAMETERS': {      '@REPORT_ID': '101',      '@SUBJECT_TYPE': '1',      '@RESPONSE_TYPE': '5'   },   'INQUIRY_REASON': {      '@CODE': '1'   },   'APPLICATION': {      '@PRODUCT': '017',      '@NUMBER': '232',      '@AMOUNT': '1',      '@CURRENCY': 'NGN'   }},'SEARCH_PARAMETERS': {   '@SEARCH-TYPE': '4',   'BVN_NO': '"+bvnRequest.getBvn()+"' }}");
            request.setUserName(username);
            request.setPassword(password);
            app.print("#########Validate BVN Request");
            app.print(request);

            Response<CRCCreditResponse> response = crcCreditBureauServiceInterface.searchByBVN(request).execute();
            app.print("#########Response:");

            if (response.isSuccessful()){
                 Object data= response.body().getConsumerSearchResultResponse();
                 app.print(data);
                 if(data!=null) {
                     return new APIResponse<>("Validation Successful", true, data);
                 }
                 else
                     return new APIResponse<>("Validation Failed ", false, null);
            } else {
                return new APIResponse<>("Request Failed "+response.code(), false, response);
            }
        }catch (Exception ex){
            ex.printStackTrace();
            return new APIResponse<>(ex.getMessage(), false, null);
        }
    }



}
