package com.syrol.paylater.entities;
import com.syrol.paylater.enums.Status;
import lombok.*;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Table(name="direct_debits")
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DirectDebit implements Serializable {

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
    @NonNull
    String payerAccount;
    @NonNull
    String requestId;
    String transactionRef;
    @NonNull
    Long  amount;
    String mandateType;
    int  maxNoOfDebits;
    @NotNull
    @Enumerated(EnumType.STRING)
    Status status;
    String mandateId;
    @NonNull
    String executedBy;
    String executedFor;
    String  startDate;
    String  endDate;
    String lastModifiedBy;
    String remark;
    @Temporal(TemporalType.TIMESTAMP)
    Date lastModifiedDate;
    @Temporal(TemporalType.TIMESTAMP)
    Date createdDate;
}