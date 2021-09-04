package com.syrol.paylater.pojos.zoho;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class ZohoSingleItemResponse {
    private int code;
    private String message;
    private ZohoItem item;
}