package com.syrol.paylater.pojos;
import lombok.Data;

@Data
public class CRCBVNSearchRequest<T> {
    protected String bvn;
    protected String dob;
}
