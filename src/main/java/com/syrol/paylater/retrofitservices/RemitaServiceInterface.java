package com.syrol.paylater.retrofitservices;
import com.syrol.paylater.pojos.DirectDebitRequest;
import retrofit2.Call;
import retrofit2.http.*;

public interface RemitaServiceInterface {

    @POST("remita/exapp/api/v1/send/api/echannelsvc/echannel/mandate/setup")
    Call<Object> directDebit(@Body DirectDebitRequest request);

}

