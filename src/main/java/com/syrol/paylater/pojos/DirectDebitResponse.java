package com.syrol.paylater.pojos;
import lombok.Data;

import java.util.List;

@Data
public class DirectDebitResponse<T> {
    String statuscode;
    String requestId;
    String mandateId;
    String status;
    List data;
 }
