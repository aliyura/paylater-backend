package com.syrol.paylater.entities;
import lombok.*;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Table(name="activity_logs")
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLog implements Serializable {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    Long id;
    @Column(columnDefinition="TEXT")
    String requestObject;
    @Column(columnDefinition="TEXT")
    String responseObject;
    String uuid;
    String contactId;
    String userName;
    String device;
    String ipAddress;
    @Column(columnDefinition="TEXT")
    String description;
    @Temporal(TemporalType.TIMESTAMP)
    Date createdAt;

    @PrePersist
    private void setCreatedAt() {
        createdAt = new Date();
    }
}