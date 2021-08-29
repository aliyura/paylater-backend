package com.syrol.paylater.pojos;

import lombok.Data;

import java.util.Date;

@Data
public class DirectDebitInstructionRequest<T> {
    private String hash;
    private String mandateId;
    private String requestId;
    private String merchantId;
    private String serviceTypeId;
    private String totalAmount;
    private String fundingAccount;
    private String fundingBankCode;
}
