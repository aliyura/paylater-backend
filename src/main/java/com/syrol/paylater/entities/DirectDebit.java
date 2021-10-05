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
    String orderReference;
    Double  amount;
    String type;
    String  description;
    @NotNull
    @Enumerated(EnumType.STRING)
    Status status;
    String reference;
    String executedBy;
    String executedFor;
    String lastModifiedBy;
    String remark;
    @Temporal(TemporalType.TIMESTAMP)
    Date lastModifiedDate;
    @Temporal(TemporalType.TIMESTAMP)
    Date createdDate;
}