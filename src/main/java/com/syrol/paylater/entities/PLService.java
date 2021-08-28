package com.syrol.paylater.entities;
import com.syrol.paylater.enums.Status;
import lombok.*;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Table(name="services")
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PLService implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    Long id;
    @NotNull
    String suid;
    @NotNull
    String title;
    @Column(columnDefinition="TEXT")
    String description;
    String displayImageUrl;
    Long min;
    Long max;
    Long ratings;
    @NotNull
    @Enumerated(EnumType.STRING)
    Status status;
    String lastModifiedBy;
    String createdBy;
    String createdByUsername;
    @Temporal(TemporalType.TIMESTAMP)
    Date lastModifiedDate;
    @Temporal(TemporalType.TIMESTAMP)
    Date createdDate;

}