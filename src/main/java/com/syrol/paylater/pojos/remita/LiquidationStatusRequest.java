package com.syrol.paylater.pojos.remita;

import lombok.Data;

@Data
public class LiquidationStatusRequest<T> {
    private String merchantId;
    private String mandateId;
    private String hash;
    private String requestId;
}
