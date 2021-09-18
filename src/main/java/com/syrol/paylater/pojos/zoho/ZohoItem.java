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
    Double rate;
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
    Double available_stock;
    Double purchase_rate;
    Double stock_on_hand;
    Boolean has_attachment;
    String status;
    String unit;
    String purchase_account_id;
    String purchase_account_name;
    String account_name;
    String show_in_storefront;
    String created_time;
    String last_modified_time;
}