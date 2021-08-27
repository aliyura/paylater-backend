package com.syrol.paylater.pojos;
import lombok.Data;

@Data
public class CRCCreditRequest<T> {
    protected String Request;
    protected String UserName;
    protected String Password;
}
