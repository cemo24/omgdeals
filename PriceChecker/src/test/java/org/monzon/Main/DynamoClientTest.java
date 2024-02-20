package org.monzon.Main;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.BatchGetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.BatchGetItemResponse;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(SystemStubsExtension.class)
public class DynamoClientTest {

    DynamoClient dynamo;

    @SystemStub
    private EnvironmentVariables environmentVariables =
            new EnvironmentVariables("DYNAMO_TABLE", "pricing");

    @BeforeEach
    public void setUp() {
        dynamo = DynamoClient.getInstance();
        dynamo.client = mock(DynamoDbClient.class);
    }

    @Test
    public void getInstanceIsSingleton() {
        DynamoClient clientTwo = DynamoClient.getInstance();
        assert (dynamo == clientTwo);
    }

    @Test
    public void filterMessagesBadMessage() {
        Wmdata badMessage = mock(Wmdata.class);

        List<Wmdata> messages = List.of(badMessage);

        List<Wmdata> results = dynamo.filterMessages(messages);
        assert (results.isEmpty());

        verify(dynamo.client, never()).batchGetItem(any(BatchGetItemRequest.class));
    }

    @Test
    public void filterMessagesGoodMessage() {

        String goodMessageRetailer = "TESTING";
        Wmdata goodMessage = mock(Wmdata.class);
        when(goodMessage.getUpc_store_retailer()).thenReturn("123-111-wm");
        when(goodMessage.getRetailer()).thenReturn(goodMessageRetailer);
        List<Wmdata> messages = List.of(goodMessage);

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

        when(dynamo.client.batchGetItem(any(BatchGetItemRequest.class))).thenReturn(dynamoResponse);

        List<Wmdata> results = dynamo.filterMessages(messages);

        assert (results.get(0).getRetailer().equals(goodMessageRetailer));
    }
}




