package org.monzon.Wally;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class Wmdata{
    @Getter private final String upc;
    @Getter private final String store;
    @Getter private final int stock;
    @Getter private Double listPrice;
    @Getter private final Double storePrice;
    @Getter private final long timestamp;
}