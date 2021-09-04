package com.syrol.paylater.retrofitservices;
import com.syrol.paylater.pojos.zoho.*;
import retrofit2.Call;
import retrofit2.http.*;

public interface ZohoItemServiceInterface {

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

