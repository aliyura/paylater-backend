package com.syrol.paylater.services;
import com.syrol.paylater.entities.DirectDebit;
import com.syrol.paylater.entities.User;
import com.syrol.paylater.enums.Status;
import com.syrol.paylater.pojos.*;
import com.syrol.paylater.pojos.remita.*;
import com.syrol.paylater.util.App;
import com.syrol.paylater.util.AppHttpClient;
import com.syrol.paylater.util.AuthDetails;
import com.syrol.paylater.util.UnsafeOkHttpClient;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Date;


@RefreshScope
@Service
@RequiredArgsConstructor
public class RemitaService {

    private final App app;
    private final AuthDetails authDetails;
    private final com.syrol.paylater.util.Response apiResponse;
    private String baseURL = "https://remitademo.net/remita/exapp/api/v1/send/api/echannelsvc/echannel/mandate";
    private String merchantId = "27768931";
    private String serviceTypeId = "35126630";
    private String apiKey = "Q1dHREVNTzEyMzR8Q1dHREVNTw==";
    private OkHttpClient okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();


    public APIResponse initiateDirectDebit(Principal principal, DirectDebitRequest request) {
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
                    AppHttpClient<DirectDebitRequest> client = new AppHttpClient<>(baseURL + "/setup");
                    String response = client.post(request);
                    if (!response.isEmpty()) {

                        String responseBody = client.getJsonResponse();
                        DirectDebitResponse debitResponse = app.getMapper().readValue(responseBody, DirectDebitResponse.class);
                        app.print("Response:");
                        app.print(debitResponse);
                        if (debitResponse.getStatuscode() != null) {
                            if (debitResponse.getStatuscode().equals("040") || debitResponse.getStatuscode().equals("074") || debitResponse.getStatuscode().equals("00")) {

                                //save debit history
                                DirectDebit debit =new DirectDebit();
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
                                debit.setLastModifiedDate(new Date());
                                debit.setCreatedDate(new Date());
                                app.print(debit);

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
                }
            }else{
                return apiResponse.failure("Authentication Required");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return apiResponse.failure("Request Failed " + ex.getMessage());
        }
    }
    public APIResponse checkDirectDebitStatus(Principal principal,DirectDebitStatusRequest request) {
        try {
            if (request.getRequestId() == null)
               return   apiResponse.failure("Request Id Required <requestId>!");
            else if (request.getMandateId() == null)
                return  apiResponse.failure("Mandate Id Required <mandateId>!");
            else {
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

                app.print("#########Check Direct Debit Status Request");
                app.print(request);
                AppHttpClient<DirectDebitStatusRequest> client = new AppHttpClient<>(baseURL + "/status");
                String response = client.post(request);
                if (!response.isEmpty()) {

                    String responseBody = client.getJsonResponse();
                    DirectDebitStatusResponse debitResponse = app.getMapper().readValue(responseBody, DirectDebitStatusResponse.class);
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
        } catch (Exception ex) {
            ex.printStackTrace();
            return apiResponse.failure("Request Failed " + ex.getMessage());
        }
    }

    public APIResponse sendDebitInstruction(Principal principal, DirectDebitInstructionRequest request){
        try {
            if (request.getMandateId() == null)
                return  apiResponse.failure("Mandate Id Required <mandateId>!");
            else if (request.getFundingAccount() == null)
                return  apiResponse.failure("Funding Account Required <fundingAccount>!");
            else if (request.getFundingBankCode() == null)
                return  apiResponse.failure("Funding Bank Code Required <fundingBankCode>!");
            else if (request.getTotalAmount() == null)
                return  apiResponse.failure("Amount Required <totalAmount>!");
            else {
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
                AppHttpClient<DirectDebitInstructionRequest> client = new AppHttpClient<>(baseURL + "/payment/send");
                String response = client.post(request);
                if (!response.isEmpty()) {

                    String responseBody = client.getJsonResponse();
                    DirectDebitResponse debitResponse = app.getMapper().readValue(responseBody, DirectDebitResponse.class);
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

    public APIResponse cancelDebitInstruction(Principal principal, DirectDebitCancelInstructionRequest request){
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
                AppHttpClient<DirectDebitCancelInstructionRequest> client = new AppHttpClient<>(baseURL + "/payment/stop");
                String response = client.post(request);
                if (!response.isEmpty()) {

                    String responseBody = client.getJsonResponse();
                    DirectDebitResponse debitResponse = app.getMapper().readValue(responseBody, DirectDebitResponse.class);
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

    public APIResponse directDebitHistory(Principal principal, DirectDebitHistoryRequest request){
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
                AppHttpClient<DirectDebitHistoryRequest> client = new AppHttpClient<>(baseURL + "/payment/history");
                String response = client.post(request);
                if (!response.isEmpty()) {

                    String responseBody = client.getJsonResponse();
                    DirectDebitResponse debitResponse =app.getMapper().readValue(responseBody, DirectDebitResponse.class);
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
