package com.syrol.paylater.retrofitservices;
import com.syrol.paylater.pojos.paystack.PaymentRequest;
import com.syrol.paylater.pojos.paystack.PaymentResponse;
import retrofit2.Call;
import retrofit2.http.*;

public interface PaymentServiceInterface {

    @POST("transaction/initialize")
    Call<PaymentResponse> initializePayment(@Header("Authorization") String token, @Body PaymentRequest request);

    @GET("transaction/verify/{reference}")
    Call<PaymentResponse> verifyPayment(@Header("Authorization") String token, @Path(value="reference") String reference);


}

