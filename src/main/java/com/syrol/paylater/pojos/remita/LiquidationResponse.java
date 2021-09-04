package com.syrol.paylater.pojos.remita;
import lombok.Data;

import java.util.List;

@Data
public class LiquidationResponse<T> {
    String statuscode;
    String requestId;
    String mandateId;
    String status;
    List data;
 }
