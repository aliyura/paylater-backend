package com.syrol.paylater.entities;
import lombok.*;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Table(name="order_items")
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @NonNull
    Long orderId;
    @NonNull
    String itemId;
    String name;
    @NonNull
    Double rate;
    int quantity;
    @Temporal(TemporalType.TIMESTAMP)
    Date createdDate;
}