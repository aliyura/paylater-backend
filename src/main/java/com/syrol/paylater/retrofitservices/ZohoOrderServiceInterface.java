package com.syrol.paylater.retrofitservices;
import com.syrol.paylater.pojos.zoho.*;
import retrofit2.Call;
import retrofit2.http.*;

public interface ZohoOrderServiceInterface {

    @POST("oauth/v2/token")
    Call<ZohoTokenResponse> generateToken(@Query("code") String code,
                               @Query("client_id") String client_id,
                               @Query("client_secret") String client_secret,
                               @Query("redirect_uri") String redirect_uri,
                               @Query("grant_type") String grant_type,
                               @Query("scope") String scope);

    @POST("api/v3/items")
    Call<ZohoSingleItemResponse> createItem(@Header("Authorization") String token, @Body ZohoItemRequest request, @Query("organization_id") String organization);

    @PUT("api/v3/items/{id}")
    Call<ZohoSingleItemResponse> updateItem(@Header("Authorization") String token, @Path("id") String id, @Body ZohoItemRequest request, @Query("organization_id") String organization);

    @DELETE("api/v3/items/{id}")
    Call<ZohoResponse> deleteItem(@Header("Authorization") String token, @Path("id") String id, @Query("organization_id") String organization);

    @POST("api/v3/items/{id}/active")
    Call<ZohoResponse> activateItem(@Header("Authorization") String token, @Path("id") String id, @Query("organization_id") String organization);

    @POST("api/v3/items/{id}/inactive")
    Call<ZohoResponse> deActivateItem(@Header("Authorization") String token, @Path("id") String id, @Query("organization_id") String organization);

    @GET("api/v3/items/{id}")
    Call<ZohoSingleItemResponse> getItem(@Header("Authorization") String token, @Path("id") String id, @Query("organization_id") String organization);

    @GET("api/v3/items")
    Call<ZohoMultiItemResponse> getItems(@Header("Authorization") String token, @Query("organization_id") String organization);
}

