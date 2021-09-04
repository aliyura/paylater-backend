package com.syrol.paylater.pojos.zoho;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ZohoContactBillingAddress {

    protected String attention;
    protected String address;
    protected String city;
    protected String state;
    protected String zip;
    protected String country;
    protected String phone;
}
