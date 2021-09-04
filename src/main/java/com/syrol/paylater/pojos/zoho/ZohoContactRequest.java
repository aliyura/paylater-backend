package com.syrol.paylater.pojos.zoho;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ZohoContactRequest {
    protected String contact_name;
    protected String company_name;
    protected String email;
    protected String phone;
    protected ZohoContactBillingAddress billing_address;
    protected ZohoContactShippingAddress shipping_address;
}