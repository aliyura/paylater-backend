package com.syrol.paylater.retrofitservices;
import com.syrol.paylater.pojos.creditregistry.CRCCreditRequest;
import com.syrol.paylater.pojos.creditregistry.CRCCreditResponse;
import retrofit2.Call;
import retrofit2.http.*;

public interface CRCCreditBureauServiceInterface {

    @POST("JsonLiveRequest/JsonService.svc/CIRRequest/ProcessRequestJson")
    Call<CRCCreditResponse> searchByBVN(@Body CRCCreditRequest request);

}

