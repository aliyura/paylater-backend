package com.syrol.paylater.entities;
import com.syrol.paylater.enums.Status;
import lombok.*;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
@Table(name="payments")
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String contactId;
    @NotNull
    String uuid;
    String orderReference;
    @NotNull
    String reference;
    String name;
    String email;
    Long paymentId;
    String domain;
    Double amount;
    Double fees;
    String gateway_response;
    String channel;
    String currency;
    String ip_address;
    @NotNull
    @Enumerated(EnumType.STRING)
    Status status;
    String paidAt;
    @Temporal(TemporalType.TIMESTAMP)
    Date paymentDate;
    @Temporal(TemporalType.TIMESTAMP)
    Date lastModifiedDate;
    @Temporal(TemporalType.TIMESTAMP)
    Date createdDate;
}