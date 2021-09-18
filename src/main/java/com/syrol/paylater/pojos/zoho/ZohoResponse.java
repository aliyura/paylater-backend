package com.syrol.paylater.pojos.zoho;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
public class ZohoResponse {
    private int code;
    private String message;
}