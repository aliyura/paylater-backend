package com.syrol.paylater.pojos;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserVerificationRequest<T> {
    private String mobileNumber;
    private Long otp;
}
