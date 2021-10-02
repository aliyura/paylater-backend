package com.syrol.paylater.pojos;
import com.syrol.paylater.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProductRequest<T> {
    String name;
    Double rate;
    String description;
    String productType;
    String vendorId;
    String reorderLevel;
    String sku;
    String inventoryAccountId;
    String thumbnail;
    String images;
    String unit;
    Status status;
    Long taxPercentage;
    String initialStock;
    String initialStockRate;
}
