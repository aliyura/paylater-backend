package com.syrol.paylater.pojos;
import com.syrol.paylater.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class OrderActivationRequest {
    private String  paymentReference;
    private Double  paidAmount;
    private String remark;
}
