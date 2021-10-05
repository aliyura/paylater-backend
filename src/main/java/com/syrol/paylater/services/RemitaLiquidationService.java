package com.syrol.paylater.services;
import com.syrol.paylater.entities.Liquidation;
import com.syrol.paylater.entities.Order;
import com.syrol.paylater.entities.ServiceRequest;
import com.syrol.paylater.entities.User;
import com.syrol.paylater.enums.Status;
import com.syrol.paylater.pojos.*;
import com.syrol.paylater.pojos.remita.*;
import com.syrol.paylater.repositories.LiquidationRepository;
import com.syrol.paylater.repositories.OrderRepository;
import com.syrol.paylater.repositories.ServiceRequestRepository;
import com.syrol.paylater.util.App;
import com.syrol.paylater.util.AppHttpClient;
import com.syrol.paylater.util.AuthDetails;
import com.syrol.paylater.util.UnsafeOkHttpClient;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Date;


@RefreshScope
@Service
@RequiredArgsConstructor
public class RemitaLiquidationService {

    private final App app;
    private final AuthDetails authDetails;
    private final com.syrol.paylater.util.Response apiResponse;
    private final LiquidationRepository liquidationRepository;
    private final ServiceRequestRepository serviceRequestRepository;
    private  final OrderRepository productOrderRepository;
    @Value("${spring.remita.baseURL}")
    private String baseURL;
    @Value("${spring.remita.merchantId}")
    private String merchantId;
    @Value("${spring.remita.serviceTypeId}")
    private String serviceTypeId;
    @Value("${spring.remita.apiKey}")
    private String apiKey;
    private OkHttpClient okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();


    public APIResponse initiateDirectDebit(Principal principal, String orderReference, LiquidationRequest request) {
        try {
            User user = authDetails.getAuthorizedUser(principal);
            app.print(user);
            if(user!=null) {

                if (request.getPayerAccount() == null)
                    return apiResponse.failure("Payer Account Required <payerAccount>!");
                else if (request.getPayerName() == null)
                    return apiResponse.failure("Payer Name Required <payerName>!");
                else if (request.getPayerBankCode() == null)
                    return apiResponse.failure("Payer Bank Code Required <payerBankCode>!");
                else if (request.getPayerEmail() == null)
                    return apiResponse.failure("Payer Email Required <payerEmail>!");
                else if (request.getPayerPhone() == null)
                    return apiResponse.failure("Payer Phone Number Required <payerPhone>!");
                else if (request.getAmount() == null)
                    return apiResponse.failure("Amount Required <amount>!");
                else if (request.getStartDate() == null)
                    return apiResponse.failure("Start Date Required <startDate>!");
                else if (request.getStartDate() == null)
                    return apiResponse.failure("End Date Required <endDate>!");
                else if (orderReference== null)
                    return apiResponse.failure("Order Reference Required!");
                else {

                    String requestId = app.generateRandomId();
                    String hashData = merchantId + serviceTypeId + requestId + request.getAmount() + apiKey;

                    app.print("####Remita  SHA512 Hash data");
                    app.print("Merchant:" + merchantId);
                    app.print("serviceTypeId:" + serviceTypeId);
                    app.print("requestId:" + requestId);
                    app.print("amount:" + request.getAmount());
                    app.print("apiKey:" + apiKey);
                    app.print(hashData);
                    String hash = app.getHashSHA512(hashData);
                    app.print(hash);
                    request.setMerchantId(merchantId);
                    request.setServiceTypeId(serviceTypeId);
                    request.setRequestId(requestId);
                    request.setHash(hash);
                    request.setMandateType("DD");
                    request.setMaxNoOfDebits(6);

                    app.print("#########Direct Debit Request");
                    app.print(request);
                    AppHttpClient<LiquidationRequest> client = new AppHttpClient<>(baseURL + "/setup");
                    String response = client.post(request);
                    if (!response.isEmpty()) {

                        String responseBody = client.getJsonResponse();
                        LiquidationResponse debitResponse = app.getMapper().readValue(responseBody, LiquidationResponse.class);
                        app.print("Response:");
                        app.print(debitResponse);
                        if (debitResponse.getStatuscode() != null) {
                            if (debitResponse.getStatuscode().equals("040") || debitResponse.getStatuscode().equals("074") || debitResponse.getStatuscode().equals("00")) {

                                //save debit history
                                Liquidation debit =new Liquidation();
                                debit.setHash(hash);
                                debit.setRequestId(debitResponse.getRequestId());
                                debit.setAmount(request.getAmount());
                                debit.setMandateId(debitResponse.getMandateId());
                                debit.setMandateType(request.getMandateType());
                                debit.setMaxNoOfDebits(request.getMaxNoOfDebits());
                                debit.setExecutedBy(user.getEmail());
                                debit.setServiceTypeId(request.getServiceTypeId());
                                debit.setPayerName(request.getPayerName());
                                debit.setPayerAccount(request.getPayerAccount());
                                debit.setPayerBankCode(request.getPayerBankCode());
                                debit.setPayerEmail(request.getPayerEmail());
                                debit.setPayerPhone(request.getPayerPhone());
                                debit.setRemark(debitResponse.getStatus());
                                debit.setStartDate(request.getStartDate());
                                debit.setEndDate(request.getEndDate());
                                debit.setStatus(Status.PA);
                                debit.setLastModifiedBy(user.getUuid());
                                debit.setOrderReference(orderReference);
                                debit.setClearedAmount(0.0);
                                debit.setUuid(user.getUuid());
                                debit.setContactId(user.getContactId());
                                debit.setLastModifiedDate(new Date());
                                debit.setCreatedDate(new Date());
                                app.print(debit);
                                return apiResponse.success(liquidationRepository.save(debit));


                            } else {
                                return apiResponse.fail(debitResponse);
                            }
                        } else {
                            return apiResponse.fail(debitResponse);
                        }
                    } else {
                        return apiResponse.failure("Request Failed");
                    }
                }
            }else{
                return apiResponse.failure("Authentication Required");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return apiResponse.failure("Request Failed " + ex.getMessage());
        }
    }
    public APIResponse checkDirectDebitStatus(Principal principal,String orderReference, LiquidationStatusRequest request) {
        try {
            if (request.getRequestId() == null)
               return   apiResponse.failure("Request Id Required <requestId>!");
            else if (request.getMandateId() == null)
                return  apiResponse.failure("Mandate Id Required <mandateId>!");
            else {
                ServiceRequest serviceRequestOrder = serviceRequestRepository.findByOrderReference(orderReference).orElse(null);
                Order productOrder = productOrderRepository.findByOrderReference(orderReference).orElse(null);

                if (serviceRequestOrder != null || productOrder != null) {

                    String hashData = request.getMandateId() + merchantId + request.getRequestId() + apiKey;
                    app.print("####Remita  SHA512 Hash data");
                    app.print("Merchant:" + merchantId);
                    app.print("requestId:" + request.getRequestId());
                    app.print("apiKey:" + apiKey);
                    app.print(hashData);
                    String hash = app.getHashSHA512(hashData);
                    app.print(hash);
                    request.setMerchantId(merchantId);
                    request.setRequestId(request.getRequestId());
                    request.setHash(hash);

                    Liquidation directDebit = liquidationRepository.findByRequestId(request.getRequestId()).orElse(null);
                    if(directDebit!=null) {

                        app.print("#########Check Direct Debit Status Request");
                        app.print(request);
                        AppHttpClient<LiquidationStatusRequest> client = new AppHttpClient<>(baseURL + "/status");
                        String response = client.post(request);
                        if (!response.isEmpty()) {

                            String responseBody = client.getJsonResponse();
                            LiquidationStatusResponse debitResponse = app.getMapper().readValue(responseBody, LiquidationStatusResponse.class);
                            app.print("Response:");
                            app.print(debitResponse);
                            if (debitResponse.getStatuscode() != null) {
                                if (debitResponse.getStatuscode().equals("040") || debitResponse.getStatuscode().equals("074") || debitResponse.getStatuscode().equals("00")) {


                                    //update service request
                                    if (serviceRequestOrder != null) {
                                        serviceRequestOrder.setClearedAmount(directDebit.getClearedAmount());
                                        serviceRequestOrder.setLastModifiedDate(new Date());
                                        if (serviceRequestOrder.getAmount().equals(directDebit.getAmount()))
                                            serviceRequestOrder.setStatus(Status.AC);
                                        serviceRequestRepository.save(serviceRequestOrder);
                                    }

                                    //update product oder
                                    if (productOrder != null) {
                                        productOrder.setPaymentReference(directDebit.getMandateId());
                                        productOrder.setPaymentDate(directDebit.getCreatedDate());
                                        productOrder.setClearedAmount(  productOrder.getClearedAmount()+directDebit.getClearedAmount());
                                        productOrder.setLastModifiedDate(new Date());
                                        if( productOrder.getAmount().equals(directDebit.getAmount()))
                                            productOrder.setStatus(Status.AC);
                                        productOrderRepository.save(productOrder);
                                    }

                                    directDebit.setClearedAmount(directDebit.getAmount());
                                    directDebit.setStatus(Status.AC);
                                    directDebit.setLastModifiedDate(new Date());
                                    liquidationRepository.save(directDebit);

                                    return apiResponse.success(debitResponse);
                                } else {
                                    return apiResponse.fail(debitResponse);
                                }
                            } else {
                                return apiResponse.fail(debitResponse);
                            }
                        } else {
                            return apiResponse.failure("Request Failed");
                        }
                    }else{
                        return apiResponse.failure("Debit Request not Found");
                    }
                }else{
                    return  apiResponse.failure("Invalid Order Reference");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return apiResponse.failure("Request Failed " + ex.getMessage());
        }
    }

    public APIResponse sendDebitInstruction(Principal principal, String orderReference, LiquidationInstructionRequest request){
        try {
            if (request.getMandateId() == null)
                return  apiResponse.failure("Mandate Id Required <mandateId>!");
            else if (request.getFundingAccount() == null)
                return  apiResponse.failure("Funding Account Required <fundingAccount>!");
            else if (request.getFundingBankCode() == null)
                return  apiResponse.failure("Funding Bank Code Required <fundingBankCode>!");
            else if (request.getTotalAmount() == null)
                return  apiResponse.failure("Amount Required <totalAmount>!");
            else if (orderReference== null)
                return  apiResponse.failure("Order Reference Required!");
            else {

                ServiceRequest serviceRequestOrder = serviceRequestRepository.findByOrderReference(orderReference).orElse(null);
                Order productOrder = productOrderRepository.findByOrderReference(orderReference).orElse(null);

                if (serviceRequestOrder != null || productOrder != null) {

                    String requestId = app.generateRandomId();
                    String hashData = merchantId + serviceTypeId + requestId + request.getTotalAmount() + apiKey;

                    app.print("####Remita  SHA512 Hash data");
                    app.print("Merchant:" + merchantId);
                    app.print("serviceTypeId:" + serviceTypeId);
                    app.print("requestId:" + requestId);
                    app.print("amount:" + request.getTotalAmount());
                    app.print("apiKey:" + apiKey);
                    app.print(hashData);
                    String hash = app.getHashSHA512(hashData);
                    app.print(hash);
                    request.setMerchantId(merchantId);
                    request.setServiceTypeId(serviceTypeId);
                    request.setRequestId(requestId);
                    request.setHash(hash);

                    app.print("#########Direct Debit Instruction Request");
                    app.print(request);
                    AppHttpClient<LiquidationInstructionRequest> client = new AppHttpClient<>(baseURL + "/payment/send");
                    String response = client.post(request);
                    if (!response.isEmpty()) {

                        String responseBody = client.getJsonResponse();
                        LiquidationResponse debitResponse = app.getMapper().readValue(responseBody, LiquidationResponse.class);
                        app.print("Response:");
                        app.print(debitResponse);
                        if (debitResponse.getStatuscode() != null) {
                            if (debitResponse.getStatuscode().equals("040") || debitResponse.getStatuscode().equals("074") || debitResponse.getStatuscode().equals("00")) {
                                return apiResponse.success(debitResponse);

                            } else {
                                return apiResponse.fail(debitResponse);
                            }
                        } else {
                            return apiResponse.fail(debitResponse);
                        }
                    } else {
                        return apiResponse.failure("Request Failed");
                    }
                } else {
                    return apiResponse.failure("Invalid Order Reference");
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
            return apiResponse.failure("Request Failed "+ex.getMessage());
        }

    }

    public APIResponse cancelDebitInstruction(Principal principal, LiquidationCancelInstructionRequest request){
        try {
            if (request.getRequestId() == null)
                return  apiResponse.failure("Request Id Required <requestId>!");
            else if (request.getMandateId() == null)
                return  apiResponse.failure("Mandate Id Required <mandateId>!");
            else if (request.getTransactionRef() == null)
                return  apiResponse.failure("Transaction Reference Required <transactionRef>!");
            else {
                String hashData = request.getTransactionRef() + merchantId + request.getRequestId() + apiKey;
                app.print("####Remita  SHA512 Hash data");
                app.print("TransactionRef:" + request.getTransactionRef());
                app.print("Merchant:" + merchantId);
                app.print("requestId:" + request.getRequestId());
                app.print("apiKey:" + apiKey);
                app.print(hashData);
                String hash = app.getHashSHA512(hashData);
                app.print(hash);
                request.setMerchantId(merchantId);
                request.setRequestId(request.getRequestId());
                request.setHash(hash);

                app.print("#########Direct Debit Cancel Instruction Request");
                app.print(request);
                AppHttpClient<LiquidationCancelInstructionRequest> client = new AppHttpClient<>(baseURL + "/payment/stop");
                String response = client.post(request);
                if (!response.isEmpty()) {

                    String responseBody = client.getJsonResponse();
                    LiquidationResponse debitResponse = app.getMapper().readValue(responseBody, LiquidationResponse.class);
                    app.print("Response:");
                    app.print(debitResponse);
                    if(debitResponse.getStatuscode()!=null) {
                        if (debitResponse.getStatuscode().equals("040") || debitResponse.getStatuscode().equals("074") || debitResponse.getStatuscode().equals("00")) {
                            return apiResponse.success(debitResponse);
                        } else {
                            return apiResponse.fail(debitResponse);
                        }
                    }else{
                        return apiResponse.fail(debitResponse);
                    }
                } else {
                    return apiResponse.failure("Request Failed");
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
            return apiResponse.failure("Request Failed "+ex.getMessage());
        }

    }

    public APIResponse directDebitHistory(Principal principal, LiquidationHistoryRequest request){
        try {
            if (request.getRequestId() == null)
                return  apiResponse.failure("Request Id Required <requestId>!");
            else if (request.getMandateId() == null)
                return  apiResponse.failure("Mandate Id Required <mandateId>!");
           else {
                String hashData = request.getMandateId() + merchantId + request.getRequestId() + apiKey;
                app.print("####Remita  SHA512 Hash data");
                app.print("MandateId:" + request.getMandateId());
                app.print("Merchant:" + merchantId);
                app.print("requestId:" + request.getRequestId());
                app.print("apiKey:" + apiKey);
                app.print(hashData);
                String hash = app.getHashSHA512(hashData);
                app.print(hash);
                request.setMerchantId(merchantId);
                request.setRequestId(request.getRequestId());
                request.setHash(hash);

                app.print("#########Direct Debit History Request");
                app.print(request);
                AppHttpClient<LiquidationHistoryRequest> client = new AppHttpClient<>(baseURL + "/payment/history");
                String response = client.post(request);
                if (!response.isEmpty()) {

                    String responseBody = client.getJsonResponse();
                    LiquidationResponse debitResponse =app.getMapper().readValue(responseBody, LiquidationResponse.class);
                    app.print("Response:");
                    app.print(debitResponse);
                    if(debitResponse.getStatuscode()!=null) {
                        if (debitResponse.getStatuscode().equals("040") || debitResponse.getStatuscode().equals("074") || debitResponse.getStatuscode().equals("00")) {
                            return apiResponse.success(debitResponse);
                        } else {
                            return apiResponse.fail(debitResponse);
                        }
                    }else{
                        return apiResponse.fail(debitResponse);
                    }
                } else {
                    return apiResponse.failure("Request Failed");
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
            return apiResponse.failure("Request Failed "+ex.getMessage());
        }

    }
}
