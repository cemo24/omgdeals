package org.monzon.Main;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class MainIntegrationTest {

    @Test
    public void testMain(){

        String json = "{\"upc_store_retailer\":\"123-100-WM\",\"upc\":\"123\",\"store\":\"1\",\"retailer\":\"WM\",\"stock\":1,\"listPrice\":1.0,\"storePrice\":10.0,\"timestamp\":0}";

        Message message = Message.builder()
                .body(json)
                .build();

        ReceiveMessageResponse messages = ReceiveMessageResponse.builder()
                .messages(Collections.singletonList(message))
                .build();

        SqsClient sqsClient = mock(SqsClient.class);
        when(sqsClient.receiveMessage(any(ReceiveMessageRequest.class))).thenReturn(messages);

        SqsMessenger sqsWrapper = SqsMessenger.getInstance();
        sqsWrapper.client = sqsClient;

        Map<String, AttributeValue> items = new HashMap<>();
        items.put("upc_store_retailer", AttributeValue.builder().s("123-111-wm").build());
        items.put("upc", AttributeValue.builder().s("123").build());
        items.put("store", AttributeValue.builder().s("100").build());
        items.put("retailer", AttributeValue.builder().s("WM").build());
        items.put("stock", AttributeValue.builder().n("1").build());
        items.put("listPrice", AttributeValue.builder().n("100.00").build());
        items.put("storePrice", AttributeValue.builder().n("9.99").build());
        items.put("timestamp", AttributeValue.builder().n("1708103641").build());

        Map<String, List<Map<String, AttributeValue>>> outer = new HashMap<>();
        outer.put("pricing", List.of(items));

        BatchGetItemResponse dynamoResponse = BatchGetItemResponse.builder()
                .responses(outer)
                .build();

        DynamoDbClient dynamoClient = mock(DynamoDbClient.class);
        when(dynamoClient.batchGetItem(any(BatchGetItemRequest.class))).thenReturn(dynamoResponse);

        BatchWriteItemResponse mockBatchWriteResponse = BatchWriteItemResponse.builder()
                .build();

        when(dynamoClient.batchWriteItem(any(BatchWriteItemRequest.class))).thenReturn(mockBatchWriteResponse);

        DynamoClient dynamoWrapper = DynamoClient.getInstance();
        dynamoWrapper.client = dynamoClient;

        Main.main(new String[0]);

        verify(dynamoClient, times(1)).batchWriteItem(any(BatchWriteItemRequest.class));
    }
}
