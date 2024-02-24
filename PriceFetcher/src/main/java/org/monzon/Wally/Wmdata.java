package org.monzon.Wally;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class Wmdata{
    @Getter private final String upc_store_retailer;
    @Getter private final String upc;
    @Getter private final String store;
    @Getter private final String retailer;
    @Getter private final int stock;
    @Getter private Double listPrice;
    @Getter private final Double storePrice;
    @Getter private final long timestamp;
    @Getter private final String title;
    @Getter private final String image;


    public Wmdata(){
        this.upc_store_retailer = null;
        this.upc = null;
        this.store = null;
        this.retailer = null;
        this.stock = 0;
        this.listPrice = 0.0;
        this.storePrice = 0.0;
        this.timestamp = 0;
        this.title = null;
        this.image = null;
    }
}