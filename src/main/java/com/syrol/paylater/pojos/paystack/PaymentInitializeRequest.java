package com.syrol.paylater.pojos.paystack;
import lombok.Data;

@Data
public class PaymentInitializeRequest<T> {
    protected Double amount;
    protected String email;
    protected String reference;
    protected  String callback_url;
}
