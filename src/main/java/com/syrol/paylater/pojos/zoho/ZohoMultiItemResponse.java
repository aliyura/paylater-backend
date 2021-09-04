package com.syrol.paylater.pojos.zoho;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class ZohoMultiItemResponse {
    private int code;
    private String message;
    private List<ZohoItem> items;
}