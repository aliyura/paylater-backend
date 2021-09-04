package com.syrol.paylater.pojos.zoho;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZohoItem implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String item_id;
    String name;
    Long rate;
    String description;
    String sku;
    String product_type;
    String avatax_use_code;
    String item_type;
    String inventory_account_id;
    String vendor_id;
    String reorder_level;
    String initial_stock;
    String initial_stock_rate;
}