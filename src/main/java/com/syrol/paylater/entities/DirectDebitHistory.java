package com.syrol.paylater.entities;
import lombok.*;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Table(name="debit_history")
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DirectDebitHistory implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    Long id;
    @NonNull
    String serviceTypeId;
    String hash;
    @NonNull
    String payerName;
    String payerEmail;
    String payerPhone;
    String payerBankCode;
    String payerAccount;
    @NonNull
    String requestId;
    @NonNull
    double amount;
    String mandateType;
    int maxNoOfDebits;
    String startDate;
    String endDate;
    @NonNull
    String executedBy;
    @NonNull
    String executedFor;
    @Temporal(TemporalType.TIMESTAMP)
    Date lastModifiedDate;
    @Temporal(TemporalType.TIMESTAMP)
    Date createdDate;
}