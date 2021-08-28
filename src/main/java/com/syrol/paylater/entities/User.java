package com.syrol.paylater.entities;
import com.syrol.paylater.enums.AccountType;
import com.syrol.paylater.enums.Status;
import lombok.*;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Table(name="users")
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    Long id;
    @NotNull
    String uuid;
    @NotNull
    String name;
    @NotNull
    String email;
    @NotNull
    String gender;
    @NotNull
    String dob;
    @NotNull
    String bvn;
    String maritalStatus;
    String sourceOfIncome;
    String monthlyIncome;
    String employerName;
    @Column(columnDefinition="TEXT")
    String employerAddress;
    String employerTelephone;
    Long code;
    @NotNull
    @Enumerated(EnumType.STRING)
    AccountType accountType;
    @NotNull
    String mobile;
    @NotNull
    String password;
    @NotNull
    @Enumerated(EnumType.STRING)
    Status status;
    @Column(columnDefinition="TEXT")
    String address;
    String country;
    String city;
    String dp;
    String newsLetter;
    String referralCode;
    String accountNumber;
    String agentId;
    String bankAccountName;
    String bankAccountNumber;
    String bankName;
    @Temporal(TemporalType.TIMESTAMP)
    Date lastLoginDate;
    @Temporal(TemporalType.TIMESTAMP)
    Date lastModifiedDate;
    @Temporal(TemporalType.TIMESTAMP)
    Date createdDate;

    public User(User user) {
        this.createdDate = new Date();
        this.lastLoginDate = new Date();
        this.lastLoginDate = new Date();
        this.status = Status.AC;
    }
}