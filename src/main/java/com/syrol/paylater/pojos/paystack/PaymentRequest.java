package com.syrol.paylater.pojos.paystack;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class PaymentRequest<T> {
    protected String amount;
    protected String email;
    protected String reference;
    protected  String callback_url;
}
