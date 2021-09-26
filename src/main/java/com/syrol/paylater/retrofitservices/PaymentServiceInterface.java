package com.syrol.paylater.retrofitservices;
import com.syrol.paylater.pojos.paystack.PaymentInitializeRequest;
import com.syrol.paylater.pojos.paystack.PaymentInitializeResponse;
import com.syrol.paylater.pojos.paystack.PaymentVerificationResponse;
import retrofit2.Call;
import retrofit2.http.*;

public interface PaymentServiceInterface {

    @POST("transaction/initialize")
    Call<PaymentInitializeResponse> initializePayment(@Header("Authorization") String token, @Body PaymentInitializeRequest request);

    @GET("transaction/verify/{reference}")
    Call<PaymentVerificationResponse> verifyPayment(@Header("Authorization") String token, @Path(value="reference") String reference);


}

