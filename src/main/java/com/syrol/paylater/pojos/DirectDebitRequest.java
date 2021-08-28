package com.syrol.paylater.pojos;
import lombok.Data;

@Data
public class DirectDebitRequest<T> {
    String merchantId;
    String serviceTypeId;
    String hash;
    String payerName;
    String payerEmail;
    String payerPhone;
    String payerBankCode;
    String payerAccount;
    String requestId;
    double amount;
    String mandateType;
    int maxNoOfDebits;
    String startDate;
    String endDate;
}
