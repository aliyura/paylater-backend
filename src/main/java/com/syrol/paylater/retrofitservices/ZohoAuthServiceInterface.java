package com.syrol.paylater.retrofitservices;
import com.syrol.paylater.pojos.zoho.*;
import retrofit2.Call;
import retrofit2.http.*;

public interface ZohoAuthServiceInterface {

    @POST("oauth/v2/token")
    Call<ZohoTokenResponse> generateAccessToKen(@Query("refresh_token") String refresh_token,
                                                @Query("client_id") String client_id,
                                                @Query("client_secret") String client_secret,
                                                @Query("redirect_uri") String redirect_uri,
                                                @Query("grant_type") String grant_type);
}

