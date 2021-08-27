package com.syrol.paylater.retrofitservices;
import com.syrol.paylater.pojos.CRCCreditRequest;
import com.syrol.paylater.pojos.CRCCreditResponse;
import com.syrol.paylater.pojos.PaymentRequest;
import com.syrol.paylater.pojos.PaymentResponse;
import retrofit2.Call;
import retrofit2.http.*;

public interface CRCCreditBureauServiceInterface {

    @POST("JsonLiveRequest/JsonService.svc/CIRRequest/ProcessRequestJson")
    Call<CRCCreditResponse> searchByBVN(@Body CRCCreditRequest request);

}

