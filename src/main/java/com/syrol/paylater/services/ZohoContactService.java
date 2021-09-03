package com.syrol.paylater.services;

import com.syrol.paylater.pojos.*;
import com.syrol.paylater.pojos.zoho.*;
import com.syrol.paylater.retrofitservices.ZohoContactServiceInterface;
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
public class ZohoContactService {


    private final App app;
    private final com.syrol.paylater.util.Response apiResponse;
    private ZohoContactServiceInterface zohoContactServiceInterface;
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
        zohoContactServiceInterface = retrofit.create(ZohoContactServiceInterface.class);
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

    public APIResponse createContact(ZohoContactRequest request) {
        try {
            app.print("#########Zoho Create Contact Request");
            app.print(request);
            String authorization = String.format("Bearer %s", accessTokenManager().getAccess_token());
            Response<ZohoSingleContactResponse> zohoResponse = zohoContactServiceInterface.createContact(authorization, request, organization).execute();
            app.print("Response data:");
            app.print(zohoResponse);
            if (zohoResponse.isSuccessful()) {
                app.print("Response Body:");
                app.print(zohoResponse.body());
                if (zohoResponse.body().getCode() == 0) {
                    return apiResponse.success(zohoResponse.body().getContact());
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




    public APIResponse updateContact(ZohoContactRequest request, String contactId) {
        try {
            app.print("#########Zoho Update Contact Request");
            app.print(request);
            String authorization = String.format("Bearer %s", accessTokenManager().getAccess_token());
            Response<ZohoSingleContactResponse> zohoResponse = zohoContactServiceInterface.updateContact(authorization, contactId, request, organization).execute();
            app.print("Response data:");
            app.print(zohoResponse);
            if (zohoResponse.isSuccessful()) {
                app.print("Response Body:");
                app.print(zohoResponse.body());
                if (zohoResponse.body().getCode() == 0) {
                    return apiResponse.success(zohoResponse.body().getContact());
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

    public APIResponse deleteContact(String contactId) {
        try {
            app.print("#########Zoho Delete Contact Request");
            app.print(contactId);
            String authorization = String.format("Bearer %s", accessTokenManager().getAccess_token());
            Response<ZohoResponse> zohoResponse = zohoContactServiceInterface.deleteContact(authorization, contactId, organization).execute();
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

    public APIResponse sendEmail(ZohoEmail request, String contactId) {
        try {
            app.print("#########Zoho Send Email to Contact Request");
            app.print(request);
            String authorization = String.format("Bearer %s", accessTokenManager().getAccess_token());
            Response<ZohoResponse> zohoResponse = zohoContactServiceInterface.sendEmail(authorization, contactId,request, organization).execute();
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

    public APIResponse activateContact(String contactId) {
        try {
            app.print("#########Zoho Activate Contact Request");
            app.print(contactId);
            String authorization = String.format("Bearer %s", accessTokenManager().getAccess_token());
            Response<ZohoResponse> zohoResponse = zohoContactServiceInterface.activateContact(authorization, contactId, organization).execute();
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

    public APIResponse deActivateContact(String contactId) {
        try {
            app.print("#########Zoho deActivate Contact Request");
            app.print(contactId);
            String authorization = String.format("Bearer %s", accessTokenManager().getAccess_token());
            Response<ZohoResponse> zohoResponse = zohoContactServiceInterface.deActivateContact(authorization, contactId, organization).execute();
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

    public APIResponse getContact(String contactId) {
        try {
            app.print("#########Zoho Get Contact Request");
            app.print(contactId);
            String authorization = String.format("Bearer %s", accessTokenManager().getAccess_token());
            Response<ZohoSingleContactResponse> zohoResponse = zohoContactServiceInterface.getContact(authorization, contactId, organization).execute();
            app.print("Response data:");
            app.print(zohoResponse);
            if (zohoResponse.isSuccessful()) {
                app.print("Response Body:");
                app.print(zohoResponse.body());
                if (zohoResponse.body().getCode() == 0) {
                    return apiResponse.success(zohoResponse.body().getContact());
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

    public APIResponse getContacts() {
        try {
            app.print("#########Zoho Get Contacts Request");
            String authorization = String.format("Bearer %s", accessTokenManager().getAccess_token());
            Response<ZohoMultiContactResponse> zohoResponse = zohoContactServiceInterface.getContacts(authorization, organization).execute();
            app.print("Response data:");
            app.print(zohoResponse);
            if (zohoResponse.isSuccessful()) {
                app.print("Response Body:");
                app.print(zohoResponse.body());
                if (zohoResponse.body().getCode() == 0) {
                    return apiResponse.success(zohoResponse.body().getContacts());
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