package com.syrol.paylater.retrofitservices;
import com.syrol.paylater.pojos.zoho.*;
import retrofit2.Call;
import retrofit2.http.*;

public interface ZohoContactServiceInterface {

    @POST("api/v3/contacts")
    Call<ZohoSingleContactResponse> createContact(@Header("Authorization") String token, @Body ZohoContactRequest request, @Query("organization_id") String organization);

    @PUT("api/v3/contacts/{id}")
    Call<ZohoSingleContactResponse> updateContact(@Header("Authorization") String token, @Path("id") String id, @Body ZohoContactRequest request, @Query("organization_id") String organization);

    @DELETE("api/v3/contacts/{id}")
    Call<ZohoResponse> deleteContact(@Header("Authorization") String token, @Path("id") String id, @Query("organization_id") String organization);

    @POST("api/v3/contacts/{id}/active")
    Call<ZohoResponse> activateContact(@Header("Authorization") String token, @Path("id") String id, @Query("organization_id") String organization);

    @POST("api/v3/contacts/{id}/inactive")
    Call<ZohoResponse> deActivateContact(@Header("Authorization") String token, @Path("id") String id, @Query("organization_id") String organization);

    @GET("api/v3/contacts/{id}")
    Call<ZohoSingleContactResponse> getContact(@Header("Authorization") String token, @Path("id") String id, @Query("organization_id") String organization);

    @GET("api/v3/contacts")
    Call<ZohoMultiContactResponse> getContacts(@Header("Authorization") String token, @Query("organization_id") String organization);

    @POST("api/v3/contacts/{id}/email")
    Call<ZohoResponse> sendEmail(@Header("Authorization") String token, @Path("id") String id, @Body ZohoEmail request,  @Query("organization_id") String organization);

}

