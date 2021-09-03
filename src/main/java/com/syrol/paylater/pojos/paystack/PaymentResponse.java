package com.syrol.paylater.pojos.paystack;
import lombok.Data;

@Data
public class PaymentResponse<T> {
    protected String status;
    protected String message;
    protected Object data;
}
