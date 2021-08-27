package com.syrol.paylater.retrofitservices;;
import com.syrol.paylater.pojos.ZohoRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ZohoServiceInterface {

    @POST("/api/v3/users")
    Call<Object> createUser(@Header("Authorization") String token, @Body ZohoRequest request, @Query("organization_id") String organization);

}

