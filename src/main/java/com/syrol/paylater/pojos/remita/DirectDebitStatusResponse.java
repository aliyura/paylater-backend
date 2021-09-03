package com.syrol.paylater.pojos.remita;
import lombok.Data;

@Data
public class DirectDebitStatusResponse<T> {

    String requestId;
    String statuscode;
    String mandateId;
    String endDate;
    String registrationDate;
    String isActive;
    String startDate;
    String status;

}