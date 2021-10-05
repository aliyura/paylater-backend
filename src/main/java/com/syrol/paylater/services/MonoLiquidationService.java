package com.syrol.paylater.services;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.syrol.paylater.entities.*;
import com.syrol.paylater.enums.Status;
import com.syrol.paylater.pojos.APIResponse;
import com.syrol.paylater.pojos.mono.MonoDirectDebitRequest;
import com.syrol.paylater.pojos.mono.MonoDirectDebitResponse;
import com.syrol.paylater.pojos.mono.MonoDirectDebitStatusRequest;
import com.syrol.paylater.pojos.mono.MonoDirectDebitStatusResponse;
import com.syrol.paylater.repositories.DirectDebitRepository;
import com.syrol.paylater.repositories.OrderRepository;
import com.syrol.paylater.repositories.ServiceRequestRepository;
import com.syrol.paylater.retrofitservices.MonoDirectDebitServiceInterface;
import com.syrol.paylater.util.App;
import com.syrol.paylater.util.AuthDetails;
import com.syrol.paylater.util.UnsafeOkHttpClient;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import javax.annotation.PostConstruct;
import java.security.Principal;
import java.util.Date;
import java.util.Optional;


@RefreshScope
@Service
@RequiredArgsConstructor
public class MonoLiquidationService {

    private final App app;
    private final AuthDetails authDetails;
    private final com.syrol.paylater.util.Response apiResponse;
    private final ServiceRequestRepository serviceRequestRepository;
    private final OrderRepository productOrderRepository;
    private final DirectDebitRepository directDebitRepository;
    private MonoDirectDebitServiceInterface monoDirectDebitServiceInterface;
    @Value("${cloud.mono.baseURL}")
    private String baseURL;
    @Value("${cloud.mono.appId}")
    private String appId;
    @Value("${cloud.mono.publicKey}")
    private String publicKey;
    @Value("${cloud.mono.secretKey}")
    private String secretKey;
    private OkHttpClient okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();


    @PostConstruct
    public void init() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        Retrofit retrofit = new Retrofit.Builder().client(okHttpClient).addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(baseURL)
                .build();
        monoDirectDebitServiceInterface = retrofit.create(MonoDirectDebitServiceInterface.class);
    }


    public APIResponse initiateDirectDebit(Principal principal, String orderReference, MonoDirectDebitRequest request) {
        try {
            User user = authDetails.getAuthorizedUser(principal);
            app.print(user);
            if (user != null) {


                String requestId = app.generateRandomId();
                request.setReference(requestId);
                request.setType("onetime-debit");

                app.print("#########Direct Debit Request");
                app.print(request);
                //save debit history
                Response<MonoDirectDebitResponse> monoResponse = monoDirectDebitServiceInterface.initializeDirectPayment(secretKey, request).execute();
                if (monoResponse.isSuccessful()) {
                    app.print("Response Body:");
                    app.print(monoResponse.body());

                    DirectDebit debit = new DirectDebit();
                    debit.setOrderReference(orderReference);
                    debit.setAmount(request.getAmount());
                    debit.setDescription(request.getDescription());
                    debit.setReference(request.getReference());
                    debit.setType(request.getType());
                    debit.setStatus(Status.PP);
                    debit.setExecutedBy(user.getEmail());
                    debit.setExecutedFor(orderReference);
                    debit.setCreatedDate(new Date());
                    directDebitRepository.save(debit);
                    return apiResponse.success(monoResponse.body());
                } else {
                    return apiResponse.failure(monoResponse.message());
                }

            } else {
                return apiResponse.failure("Authentication Required");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return apiResponse.failure("Request Failed " + ex.getMessage());
        }
    }

    public APIResponse checkDirectDebitStatus(Principal principal, String debitReference) {
        try {
            if (debitReference == null)
                return apiResponse.failure("Debit Reference Id Required!");
            else {

                DirectDebit directDebit = directDebitRepository.findByReference(debitReference).orElse(null);
                if (directDebit != null) {

                    MonoDirectDebitStatusRequest directDebitStatusRequest = new MonoDirectDebitStatusRequest();
                    directDebitStatusRequest.setReference(debitReference);

                    Response<MonoDirectDebitStatusResponse> monoResponse = monoDirectDebitServiceInterface.checkPaymentStatus(secretKey, directDebitStatusRequest).execute();
                    if (monoResponse.isSuccessful()) {

                        ServiceRequest serviceRequestOrder = serviceRequestRepository.findByOrderReference(directDebit.getOrderReference()).orElse(null);
                        Order productOrder = productOrderRepository.findByOrderReference(directDebit.getOrderReference()).orElse(null);
                        if (serviceRequestOrder != null || productOrder != null) {


                            //update service request
                            if (serviceRequestOrder != null) {
                                serviceRequestOrder.setClearedAmount(directDebit.getAmount());
                                serviceRequestOrder.setLastModifiedDate(new Date());
                                if (serviceRequestOrder.getAmount().equals(directDebit.getAmount()))
                                    serviceRequestOrder.setStatus(Status.AC);
                                serviceRequestRepository.save(serviceRequestOrder);
                            }

                            //update product oder
                            if (productOrder != null) {
                                productOrder.setPaymentReference(directDebit.getReference());
                                productOrder.setPaymentDate(directDebit.getCreatedDate());
                                productOrder.setClearedAmount(productOrder.getClearedAmount() + directDebit.getAmount());
                                productOrder.setLastModifiedDate(new Date());
                                if (productOrder.getAmount().equals(directDebit.getAmount()))
                                    productOrder.setStatus(Status.AC);
                                productOrderRepository.save(productOrder);
                            }

                            directDebit.setStatus(Status.AC);
                            directDebit.setLastModifiedDate(new Date());
                            directDebitRepository.save(directDebit);

                            return apiResponse.success(monoResponse);
                        } else {
                            return apiResponse.failure("Order not found on the System!");
                        }
                    } else {
                        return apiResponse.failure("Direct debit status confirmation failed!");
                    }

                } else {
                    return apiResponse.failure("Order not found!");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return apiResponse.failure("Request Failed " + ex.getMessage());
        }

    }

    public APIResponse<Page<DirectDebit>> directDebitHistory(Pageable pageable){
        Page<DirectDebit> directDebitPage=directDebitRepository.findAll(pageable);
        if(directDebitPage.isEmpty()){
            return apiResponse.failure("No Direct Debit Found");
        }else{
            return  apiResponse.success(directDebitPage);
        }
    }

    public APIResponse<Page<DirectDebit>> directDebitHistoryByStatus(Pageable pageable, Status status){
        Optional<Page<DirectDebit>> directDebitPage=directDebitRepository.findByStatus(status, pageable);
        if(directDebitPage.isEmpty()){
            return apiResponse.failure("No Direct Debit Found");
        }else{
            return  apiResponse.success(directDebitPage);
        }
    }
}