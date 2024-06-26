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
    @Getter private final String title;
    @Getter private final String image;

    public Wmdata() {
        this.upc_store_retailer = "";
        this.upc = "";
        this.store = "";
        this.retailer = "";
        this.stock = 0;
        this.listPrice = 0.0;
        this.storePrice = 0.0;
        this.timestamp = 0L;
        this.title = "";
        this.image = "";
    }

    public Map<String, AttributeValue> toMap() {
        return Map.of(
                "upc_store_retailer", AttributeValue.builder().s(upc_store_retailer).build(),
                "upc", AttributeValue.builder().s(upc).build(),
                "store", AttributeValue.builder().s(store).build(),
                "retailer", AttributeValue.builder().s(retailer).build(),
                "stock", AttributeValue.builder().n(String.valueOf(stock)).build(),
                "listPrice", AttributeValue.builder().n(String.valueOf(listPrice)).build(),
                "storePrice", AttributeValue.builder().n(String.valueOf(storePrice)).build(),
                "timestamp", AttributeValue.builder().n(String.valueOf(timestamp)).build(),
                "title", AttributeValue.builder().s(String.valueOf(title)).build(),
                "image", AttributeValue.builder().s(String.valueOf(image)).build()
        );
    }
}