package com.syrol.paylater.pojos.remita;
import lombok.Data;

@Data
public class LiquidationRequest<T> {
    String merchantId;
    String serviceTypeId;
    String hash;
    String payerName;
    String payerEmail;
    String payerPhone;
    String payerBankCode;
    String payerAccount;
    String requestId;
    Double amount;
    String mandateType;
    int maxNoOfDebits;
    String startDate;
    String endDate;
}
