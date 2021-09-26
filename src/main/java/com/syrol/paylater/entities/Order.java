package com.syrol.paylater.entities;
import com.syrol.paylater.enums.OrderDeliveryMethod;
import com.syrol.paylater.enums.OrderPaymentMethod;
import com.syrol.paylater.enums.Status;
import com.syrol.paylater.pojos.zoho.ZohoItem;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Table(name="orders")
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    Long id;
    @NotNull
    String contactId;
    @NotNull
    String uuid;
    String orderReference;
    Double amount;
    Double clearedAmount;
    int deliveryFee;
    @NotNull
    String name;
    @NotNull
    String email;
    @Column(columnDefinition="TEXT")
    String statusReason;
    @Column(columnDefinition="TEXT")
    String remark;
    @Column(columnDefinition="TEXT")
    String shippingAddress;
    String phoneNumber;
    String alternativePhoneNumber;
    @NotNull
    @Enumerated(EnumType.STRING)
    OrderPaymentMethod paymentMethod;
    @NotNull
    @Enumerated(EnumType.STRING)
    OrderDeliveryMethod deliveryMethod;
    @NotNull
    @Enumerated(EnumType.STRING)
    Status status;
    String country;
    String paymentReference;
    String city;
    String agentId;
    @Temporal(TemporalType.TIMESTAMP)
    Date paymentDate;
    @Temporal(TemporalType.TIMESTAMP)
    Date lastModifiedDate;
    @Temporal(TemporalType.TIMESTAMP)
    Date createdDate;
}