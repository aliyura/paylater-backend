
package com.syrol.paylater.entities;
import lombok.*;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Table(name="verification_requests")
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationRequest implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    Long id;
    String username;
    Long verificationCode;
    @Temporal(TemporalType.TIMESTAMP)
    Date createdDate;
}