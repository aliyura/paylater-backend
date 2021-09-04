package com.syrol.paylater.pojos.paystack;
import lombok.Data;

@Data
public class PaymentVerificationResponse<T> {
    protected String status;
    protected String message;
    protected PaymentVerificationResponseData data;
}
