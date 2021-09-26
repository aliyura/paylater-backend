package com.syrol.paylater.pojos.zoho;

import com.syrol.paylater.entities.OrderItem;
import com.syrol.paylater.enums.OrderDeliveryMethod;
import com.syrol.paylater.enums.OrderPaymentMethod;
import com.syrol.paylater.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
public class ZohoOrderRequest {
    String contactId;
    List<OrderItem> items;
    int deliveryFee;
    String name;
    String email;
    String remark;
    String shippingAddress;
    String phoneNumber;
    String alternativePhoneNumber;
    OrderPaymentMethod paymentMethod;
    OrderDeliveryMethod deliveryMethod;
    String country;
    String city;
    String agentId;
}