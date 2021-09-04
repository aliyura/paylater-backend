package com.syrol.paylater.pojos.paystack;
import lombok.Data;

@Data
public class PaymentVerificationResponseData<T> {
    protected  Long id;
    protected String domain;
    protected String status;
    protected String reference;
    protected Double amount;
    protected Double fees;
    protected String gateway_response;
    protected String channel;
    protected String currency;
    protected String ip_address;
    protected String paid_at;
    protected  String transaction_date;
}
