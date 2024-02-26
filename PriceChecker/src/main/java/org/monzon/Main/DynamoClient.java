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
    public DynamoDbClient client;

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

        HashMap<String, Wmdata> recentMessages = new HashMap<>();

        for (Wmdata wmdata : messages) {
            if(wmdata.getUpc_store_retailer() == null || wmdata.getUpc_store_retailer().isEmpty()){
                continue;
            }
            recentMessages.put(wmdata.getUpc_store_retailer(), wmdata);
        }

        if(recentMessages.isEmpty()){
            return new ArrayList<>();
        }

        List<Map<String, AttributeValue>> existingItems = recentMessages.keySet().stream()
                .map(primaryKeyValue -> {
                    QueryRequest queryRequest = QueryRequest.builder()
                            .tableName(TABLE_NAME)
                            .keyConditionExpression("upc_store_retailer = :value")
                            .expressionAttributeValues(Collections.singletonMap(":value", AttributeValue.builder().s(primaryKeyValue).build()))
                            .build();

                    QueryResponse queryResponse = this.client.query(queryRequest);
                    return queryResponse.items();
                })
                .flatMap(List::stream)
                .collect(Collectors.toList());


        Map<String, Wmdata> existingMap = existingItems.parallelStream()
                .flatMap(item -> {
                    Wmdata wmdata = new Wmdata(
                            item.get("upc_store_retailer").s(),
                            item.get("upc").s(),
                            item.get("store").s(),
                            item.get("retailer").s(),
                            Integer.parseInt(item.get("stock").n()),
                            Double.parseDouble(item.get("listPrice").n()),
                            Double.parseDouble(item.get("storePrice").n()),
                            Long.parseLong(item.get("timestamp").n()),
                            item.get("title").s(),
                            item.get("image").s()
                    );
                    return Map.of(item.get("upc_store_retailer").s(), wmdata).entrySet().stream();
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        List<Wmdata> formattedItems = new ArrayList<>();

        for(var message : recentMessages.entrySet()){
            Wmdata itemExists = existingMap.getOrDefault(message.getKey(), null);
            if(itemExists == null || message.getValue().getStorePrice() < itemExists.getStorePrice()) {
                formattedItems.add(message.getValue());
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
        var unprocessedItems = response.unprocessedItems();
        if(unprocessedItems.size() > 0){
            logger.error("Batch PUT incomplete");
        }else{
            logger.info("Batch PUT complete");
        }
    }
}
