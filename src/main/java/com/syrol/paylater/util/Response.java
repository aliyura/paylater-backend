package com.syrol.paylater.util;
import com.syrol.paylater.pojos.APIResponse;
import org.springframework.stereotype.Service;

@Service
public class Response<T> {

    public APIResponse<T> success(T payload) {
        return new APIResponse<T>("Request Successful", true, payload);
    }

    public APIResponse<String> failure(String message) {
        return new APIResponse<String>(message, false,null);
    }
}
