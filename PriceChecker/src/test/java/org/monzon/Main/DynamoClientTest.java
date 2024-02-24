package org.monzon.Main;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;

import java.util.Arrays;
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

        DynamoDbClient mockClient = mock(DynamoDbClient.class);

        Map<String, AttributeValue> mockedItem = new HashMap<>();
        mockedItem.put("upc_store_retailer", AttributeValue.builder().s("example_100_WM").build());
        mockedItem.put("upc", AttributeValue.builder().s("123").build());
        mockedItem.put("store", AttributeValue.builder().s("100").build());
        mockedItem.put("retailer", AttributeValue.builder().s("WM").build());
        mockedItem.put("stock", AttributeValue.builder().n("100").build());
        mockedItem.put("listPrice", AttributeValue.builder().n("100.00").build());
        mockedItem.put("storePrice", AttributeValue.builder().n("10.00").build());
        mockedItem.put("timestamp", AttributeValue.builder().n("1708563838").build());
        mockedItem.put("title", AttributeValue.builder().s("product").build());
        mockedItem.put("image", AttributeValue.builder().s("www").build());
        List<Map<String, AttributeValue>> mockedItems = Arrays.asList(mockedItem);

        QueryResponse mockQueryResponse = QueryResponse.builder()
                .count(mockedItems.size())
                .items(mockedItems)
                .build();

        when(mockClient.query(any(QueryRequest.class))).thenReturn(mockQueryResponse);
        dynamo.client = mockClient;

        List<Wmdata> results = dynamo.filterMessages(messages);

        assert (results.get(0).getRetailer().equals(goodMessageRetailer));
    }
}




