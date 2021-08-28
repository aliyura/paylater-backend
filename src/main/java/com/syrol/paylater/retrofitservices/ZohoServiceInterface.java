package com.syrol.paylater.retrofitservices;;
import com.syrol.paylater.pojos.ZohoRequest;
import com.syrol.paylater.pojos.ZohoTokenResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ZohoServiceInterface {

    @POST("oauth/v2/token")
    Call<Object> generateToken(@Query("code") String code,
                               @Query("client_id") String client_id,
                               @Query("client_secret") String client_secret,
                               @Query("redirect_uri") String redirect_uri,
                               @Query("grant_type") String grant_type,
                               @Query("scope") String scope);

    @POST("/api/v3/users")
    Call<Object> createUser(@Header("Authorization") String token, @Body ZohoRequest request, @Query("organization_id") String organization);

}

