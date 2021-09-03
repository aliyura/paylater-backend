package com.syrol.paylater.pojos.remita;

import lombok.Data;

@Data
public class DirectDebitHistoryRequest<T> {
    private String merchantId;
    private String mandateId;
    private String hash;
    private String requestId;
}
