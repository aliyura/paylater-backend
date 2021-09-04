package com.syrol.paylater.pojos.zoho;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ZohoContactShippingAddress {
    private String attention;
    private String address;
    private String city;
    private String state;
    private String zip;
    private String country;
    private String phone;
}
