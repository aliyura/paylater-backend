package com.syrol.paylater.pojos.zoho;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
public class ZohoTokenResponse {
    private String access_token;
    private String api_domain;
    private String token_type;
    private Long expires_in;
    private String error;

}