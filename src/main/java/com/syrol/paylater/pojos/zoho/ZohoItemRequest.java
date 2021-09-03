package com.syrol.paylater.pojos.zoho;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ZohoItemRequest {
    protected String name;
    protected Long rate;
    protected String description;
    protected String sku;
    protected String product_type;
    protected String avatax_use_code;
    protected String item_type;
    protected String inventory_account_id;
    protected String vendor_id;
    protected String reorder_level;
    protected String initial_stock;
    protected String initial_stock_rate;
}