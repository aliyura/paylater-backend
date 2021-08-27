package com.syrol.paylater.pojos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ZohoCreateUserRequest {

    protected String name;
    protected String email;
    protected  String role_id;
    protected String  cost_rate;
}
