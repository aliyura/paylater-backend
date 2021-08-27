package com.syrol.paylater.pojos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ZohoUser {
    protected String name;
    protected String email;
    protected String user_id;
    protected String user_role;
    protected String status;
    protected Boolean is_current_user;
    protected Boolean is_customer_segmented;
    protected Boolean is_vendor_segmented;
    protected String photo_url;
    protected String user_type;
}
