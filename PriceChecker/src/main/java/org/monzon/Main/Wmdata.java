package org.monzon.Main;

import lombok.AllArgsConstructor;
import lombok.Getter;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

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

    public Wmdata() {
        this.upc_store_retailer = null;
        this.upc = null;
        this.store = null;
        this.retailer = null;
        this.stock = 0;
        this.listPrice = null;
        this.storePrice = null;
        this.timestamp = 0L;
    }

    public Map<String, AttributeValue> toMap() {
        return Map.of(
                "upc_store_retailer", AttributeValue.builder().s(upc_store_retailer).build(),
                "upc", AttributeValue.builder().s(upc).build(),
                "store", AttributeValue.builder().s(store).build(),
                "retailer", AttributeValue.builder().s(store).build(),
                "stock", AttributeValue.builder().n(String.valueOf(stock)).build(),
                "listPrice", AttributeValue.builder().n(String.valueOf(listPrice)).build(),
                "storePrice", AttributeValue.builder().n(String.valueOf(storePrice)).build(),
                "timestamp", AttributeValue.builder().n(String.valueOf(timestamp)).build()
        );
    }
}