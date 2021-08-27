package com.syrol.paylater.pojos;
import com.syrol.paylater.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class LoginResponse {
    private String accessToken;
    private User user;
}
