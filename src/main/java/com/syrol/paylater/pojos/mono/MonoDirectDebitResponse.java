package com.syrol.paylater.pojos.mono;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonoDirectDebitResponse<T> {
    private String id;
    private String type;
    private Double amount;
    private String description;
    private String reference;
    private String payment_link;
    private String created_at;
    private String updated_at;
}
