package com.syrol.paylater.pojos;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class ZohoTokenResponse {
    private String access_token;
    private String api_domain;
    private String token_type;
    private Long expires_in;
    private String error;

}