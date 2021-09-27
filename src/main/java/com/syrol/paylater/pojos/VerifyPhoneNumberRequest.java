package com.syrol.paylater.pojos;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerifyPhoneNumberRequest<T> {
    private String mobileNumber;
}
