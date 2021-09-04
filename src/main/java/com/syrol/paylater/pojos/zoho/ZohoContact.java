package com.syrol.paylater.pojos.zoho;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ZohoContact {
    protected String contact_id;
    protected String contact_name;
    protected String company_name;
    protected String email;
    protected String phone;
    protected String contact_type;
    protected String customer_sub_type;
    protected int credit_limit;
    protected String is_portal_enabled;
    protected String status;
    protected ZohoContactBillingAddress billing_address;
    protected ZohoContactShippingAddress shipping_address;
}