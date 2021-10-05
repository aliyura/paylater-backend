package com.syrol.paylater.pojos.mono;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonoDirectDebitRequest<T> {
    private Double amount;
    private String type;
    private String description;
    private String reference;
    private String redirect_url;
}
