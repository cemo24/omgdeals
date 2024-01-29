package org.monzon.Main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.*;
import java.util.stream.Collectors;

public class DynamoClient {

    private static String TABLE_NAME = System.getenv("DYNAMO_TABLE");
    private static final Logger logger = LoggerFactory.getLogger(DynamoClient.class);
    private static DynamoClient instance;
    private DynamoDbClient client;

    private DynamoClient() {
        client = DynamoDbClient.builder()
                .region(Region.US_EAST_2)
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .build();
    }

    public static DynamoClient getInstance() {
        if (instance == null) {
            synchronized (DynamoClient.class) {
                if (instance == null) {
                    instance = new DynamoClient();
                    logger.info("DynamoDb Initialized");
                }
            }
        }
        return instance;
    }

    public List<Wmdata> filterMessages(List<Wmdata> messages){

        List<Map<String, AttributeValue>> fetchedKeys = new ArrayList<>();

        for (Wmdata wmdata : messages) {
            Map<String, AttributeValue> key = new HashMap<>();
            key.put("upc-store-retailer", AttributeValue.builder().s(wmdata.getUpc_store_retailer()).build());
            fetchedKeys.add(key);
        }

        BatchGetItemRequest batchGetItemRequest = BatchGetItemRequest.builder()
                .requestItems(Collections.singletonMap(TABLE_NAME,
                        KeysAndAttributes.builder()
                                .keys(fetchedKeys)
                                .build())).build();

        BatchGetItemResponse batchGetItemResponse = this.client.batchGetItem(batchGetItemRequest);
        List<Map<String, AttributeValue>> existingItems = batchGetItemResponse.responses().get(TABLE_NAME);

        List<Wmdata> formattedItems = new ArrayList<>();

        for (Wmdata newItem : messages) {

            Wmdata itemExists = existingItems.stream()
                    .filter(item -> newItem.getUpc_store_retailer().equals(item.get("upc-store-retailer").s()))
                    .findFirst()
                    .map(item -> new Wmdata(
                            item.get("upc-store-retailer").s(),
                            item.get("upc").s(),
                            item.get("store").s(),
                            item.get("retailer").s(),
                            Integer.parseInt(item.get("stock").n()),
                            Double.parseDouble(item.get("listPrice").n()),
                            Double.parseDouble(item.get("storePrice").n()),
                            Long.parseLong(item.get("timestamp").n())
                    ))
                    .orElse(null);


            if(itemExists == null || newItem.getStorePrice() < itemExists.getStorePrice()) {
                formattedItems.add(newItem);
            }
        }
        return formattedItems;
    }

    public void putItems(List<Wmdata> items){

        List<WriteRequest> writeRequests = items.stream()
                .map(wmdata -> WriteRequest.builder().putRequest(
                        PutRequest.builder()
                                .item(wmdata.toMap())
                                .build()).build()).collect(Collectors.toList());

        Map<String, List<WriteRequest>> requestItems = Collections.singletonMap(TABLE_NAME, writeRequests);

        BatchWriteItemRequest batchWriteItemRequest = BatchWriteItemRequest.builder()
                .requestItems(requestItems)
                .build();

        BatchWriteItemResponse response = client.batchWriteItem(batchWriteItemRequest);

        if(response.hasUnprocessedItems()){
            logger.error("Batch PUT incomplete");
        }else{
            logger.info("Batch PUT complete");
        }
    }
}
