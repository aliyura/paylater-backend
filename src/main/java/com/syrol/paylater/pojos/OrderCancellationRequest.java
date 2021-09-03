package com.syrol.paylater.pojos;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class OrderCancellationRequest {
    private String reason;
}
