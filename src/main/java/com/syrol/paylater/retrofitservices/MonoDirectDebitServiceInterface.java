package com.syrol.paylater.retrofitservices;
import com.syrol.paylater.pojos.mono.*;
import retrofit2.Call;
import retrofit2.http.*;

public interface MonoDirectDebitServiceInterface {

    @POST("payments/initiate")
    Call<MonoDirectDebitResponse> initializeDirectPayment(@Header("mono-sec-key") String secret, @Body MonoDirectDebitRequest request);

    @POST("payments/verify")
    Call<MonoDirectDebitStatusResponse> checkPaymentStatus(@Header("mono-sec-key") String secret, @Body MonoDirectDebitStatusRequest request);

}



