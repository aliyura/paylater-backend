package com.syrol.paylater.pojos.remita;

import lombok.Data;

@Data
public class DirectDebitCancelInstructionRequest<T> {

    private String merchantId;
    private String mandateId;
    private String hash;
    private String transactionRef;
    private String requestId;

}
