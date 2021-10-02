package com.syrol.paylater.entities;

import com.syrol.paylater.enums.Status;
import lombok.*;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Table(name="products")
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @NonNull
    String itemId;
    String name;
    @NonNull
    Double rate;
    @Column(columnDefinition="TEXT")
    String description;
    String productType;
    String vendorId;
    String reorderLevel;
    String sku;
    String inventoryAccountId;
    String thumbnail;
    String images;
    String unit;
    @NotNull
    @Enumerated(EnumType.STRING)
    Status status;
    Long taxPercentage;
    String initialStock;
    String initialStockRate;
    String createdBy;
    String createdByUsername;
    @Temporal(TemporalType.TIMESTAMP)
    Date createdDate;
    @Temporal(TemporalType.TIMESTAMP)
    Date lastModifiedDate;
}