
package com.syrol.paylater.entities;

import com.syrol.paylater.enums.ServiceTenureType;
import com.syrol.paylater.enums.Status;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Table(name="service_request")
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceRequest implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    Long id;
    @NotNull
    String suid;
    @NotNull
    String uuid;
    @NotNull
    String sruid;
    @Column(columnDefinition="TEXT")
    String remark;
    String userName;
    String userEmail;
    @NotNull
    Long amount;
    @NotNull
    @Enumerated(EnumType.STRING)
    Status status;
    @NotNull
    @Enumerated(EnumType.STRING)
    ServiceTenureType tenure;
    String lastModifiedBy;
    @Temporal(TemporalType.TIMESTAMP)
    Date lastModifiedDate;
    @Temporal(TemporalType.TIMESTAMP)
    Date createdDate;
}