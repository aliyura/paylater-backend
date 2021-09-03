package com.syrol.paylater.services;

import com.syrol.paylater.pojos.APIResponse;
import com.syrol.paylater.pojos.zoho.*;
import com.syrol.paylater.retrofitservices.ZohoItemServiceInterface;
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
public class ZohoItemService {

    private final App app;
    private final com.syrol.paylater.util.Response apiResponse;
    private ZohoItemServiceInterface zohoItemServiceInterface;
    private OkHttpClient okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();
    @Value("${spring.zoho.baseURL}")
    private String baseURL;
    @Value("${spring.zoho.organization}")
    private String organization;
    @Value("${spring.zoho.clientId}")
    private String clientId;
    @Value("${spring.zoho.clientSecret}")
    private String clientSecret;
    @Value("${spring.zoho.grantType}")
    private String grantType;
    @Value("${spring.zoho.scope}")
    private String scope;
    @Value("${spring.zoho.accessType}")
    private String accessType;
    @Value("${spring.zoho.redirectURL}")
    private String redirectURL;
    @Value("${spring.zoho.code}")
    private String code;
    @Value("${spring.zoho.accessToken}")
    private String accessToken;


    @PostConstruct
    public void init() {
        Retrofit retrofit = new Retrofit.Builder().client(okHttpClient).addConverterFactory(GsonConverterFactory.create())
                .baseUrl(baseURL)
                .build();
        zohoItemServiceInterface = retrofit.create(ZohoItemServiceInterface.class);
    }

    public ZohoTokenResponse accessTokenManager() {
        try {
//            app.print("#########Generating Zoho access token");
//            Response<ZohoTokenResponse> tokenResponseResponse = zohoServiceInterface.generateToken(code, clientId, clientSecret, redirectURL, grantType, scope).execute();
//            if(tokenResponseResponse.isSuccessful()){
//                app.print("Response:");
//                app.print(tokenResponseResponse.body());
//                return  tokenResponseResponse.body();
//            }
//            app.print("Response:");
//            app.print(tokenResponseResponse.headers());
//            app.print(tokenResponseResponse.code());
            ZohoTokenResponse tokenResponse = new ZohoTokenResponse();
            tokenResponse.setAccess_token(accessToken);
            return tokenResponse;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public APIResponse createItem(ZohoItemRequest request) {
        try {
            app.print("#########Zoho Create Item Request");
            app.print(request);
            String authorization = String.format("Bearer %s", accessTokenManager().getAccess_token());
            Response<ZohoSingleItemResponse> zohoResponse = zohoItemServiceInterface.createItem(authorization, request, organization).execute();
            app.print("Response data:");
            app.print(zohoResponse);
            if (zohoResponse.isSuccessful()) {
                app.print("Response Body:");
                app.print(zohoResponse.body());
                if (zohoResponse.body().getCode() == 0) {
                    return apiResponse.success(zohoResponse.body().getItem());
                } else {
                    return apiResponse.failure(zohoResponse.body().getMessage());
                }
            } else {
                return apiResponse.failure(zohoResponse.message());
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return apiResponse.failure(ex.getMessage());
        }
    }

    public APIResponse updateItem(ZohoItemRequest request, String ItemId) {
        try {
            app.print("#########Zoho Update Item Request");
            app.print(request);
            String authorization = String.format("Bearer %s", accessTokenManager().getAccess_token());
            Response<ZohoSingleItemResponse> zohoResponse = zohoItemServiceInterface.updateItem(authorization, ItemId, request, organization).execute();
            app.print("Response data:");
            app.print(zohoResponse);
            if (zohoResponse.isSuccessful()) {
                app.print("Response Body:");
                app.print(zohoResponse.body());
                if (zohoResponse.body().getCode() == 0) {
                    return apiResponse.success(zohoResponse.body().getItem());
                } else {
                    return apiResponse.failure(zohoResponse.body().getMessage());
                }
            } else {
                return apiResponse.failure(zohoResponse.message());
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return apiResponse.failure(ex.getMessage());
        }
    }

    public APIResponse deleteItem(String ItemId) {
        try {
            app.print("#########Zoho Delete Item Request");
            app.print(ItemId);
            String authorization = String.format("Bearer %s", accessTokenManager().getAccess_token());
            Response<ZohoResponse> zohoResponse = zohoItemServiceInterface.deleteItem(authorization, ItemId, organization).execute();
            app.print("Response data:");
            app.print(zohoResponse);
            if (zohoResponse.isSuccessful()) {
                app.print("Response Body:");
                app.print(zohoResponse.body());
                if (zohoResponse.body().getCode() == 0) {
                    return apiResponse.success(zohoResponse.body().getMessage());
                } else {
                    return apiResponse.failure(zohoResponse.body().getMessage());
                }
            } else {
                return apiResponse.failure(zohoResponse.message());
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return apiResponse.failure(ex.getMessage());
        }
    }

    public APIResponse activateItem(String ItemId) {
        try {
            app.print("#########Zoho Activate Item Request");
            app.print(ItemId);
            String authorization = String.format("Bearer %s", accessTokenManager().getAccess_token());
            Response<ZohoResponse> zohoResponse = zohoItemServiceInterface.activateItem(authorization, ItemId, organization).execute();
            app.print("Response data:");
            app.print(zohoResponse);
            if (zohoResponse.isSuccessful()) {
                app.print("Response Body:");
                app.print(zohoResponse.body());
                if (zohoResponse.body().getCode() == 0) {
                    return apiResponse.success(zohoResponse.body().getMessage());
                } else {
                    return apiResponse.failure(zohoResponse.body().getMessage());
                }
            } else {
                return apiResponse.failure(zohoResponse.message());
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return apiResponse.failure(ex.getMessage());
        }
    }

    public APIResponse deActivateItem(String ItemId) {
        try {
            app.print("#########Zoho deActivate Item Request");
            app.print(ItemId);
            String authorization = String.format("Bearer %s", accessTokenManager().getAccess_token());
            Response<ZohoResponse> zohoResponse = zohoItemServiceInterface.deActivateItem(authorization, ItemId, organization).execute();
            app.print("Response data:");
            app.print(zohoResponse);
            if (zohoResponse.isSuccessful()) {
                app.print("Response Body:");
                app.print(zohoResponse.body());
                if (zohoResponse.body().getCode() == 0) {
                    return apiResponse.success(zohoResponse.body().getMessage());
                } else {
                    return apiResponse.failure(zohoResponse.body().getMessage());
                }
            } else {
                return apiResponse.failure(zohoResponse.message());
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return apiResponse.failure(ex.getMessage());
        }
    }

    public APIResponse getItem(String ItemId) {
        try {
            app.print("#########Zoho Get Item Request");
            app.print(ItemId);
            String authorization = String.format("Bearer %s", accessTokenManager().getAccess_token());
            Response<ZohoSingleItemResponse> zohoResponse = zohoItemServiceInterface.getItem(authorization, ItemId, organization).execute();
            app.print("Response data:");
            app.print(zohoResponse);
            if (zohoResponse.isSuccessful()) {
                app.print("Response Body:");
                app.print(zohoResponse.body());
                if (zohoResponse.body().getCode() == 0) {
                    return apiResponse.success(zohoResponse.body().getItem());
                } else {
                    return apiResponse.failure(zohoResponse.body().getMessage());
                }
            } else {
                return apiResponse.failure(zohoResponse.message());
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return apiResponse.failure(ex.getMessage());
        }
    }

    public APIResponse getItems() {
        try {
            app.print("#########Zoho Get Items Request");
            String authorization = String.format("Bearer %s", accessTokenManager().getAccess_token());
            Response<ZohoMultiItemResponse> zohoResponse = zohoItemServiceInterface.getItems(authorization, organization).execute();
            app.print("Response data:");
            app.print(zohoResponse);
            if (zohoResponse.isSuccessful()) {
                app.print("Response Body:");
                app.print(zohoResponse.body());
                if (zohoResponse.body().getCode() == 0) {
                    return apiResponse.success(zohoResponse.body().getItems());
                } else {
                    return apiResponse.failure(zohoResponse.body().getMessage());
                }
            } else {
                return apiResponse.failure(zohoResponse.message());
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return apiResponse.failure(ex.getMessage());
        }
    }
}