package org.monzon.Main;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

public class SqsMessengerTest {

    SqsMessenger sqs;
    String json;

    @BeforeEach
    public void setUp() {

        sqs = SqsMessenger.getInstance();

        Wmdata data = new Wmdata();
        ObjectMapper mapper = new ObjectMapper();

        try {
            json = mapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getInstanceIsSingleton() {
        SqsMessenger clientOne = SqsMessenger.getInstance();
        SqsMessenger clientTwo = SqsMessenger.getInstance();
        assert (clientOne == clientTwo);
    }
    @Test
    public void getMessagesIsEmpty() {
        SqsClient mockClient = mock(SqsClient.class);

        ReceiveMessageResponse emptyMessageResponse = ReceiveMessageResponse.builder()
                .messages(Collections.emptyList())
                .build();

        when(mockClient.receiveMessage(any(ReceiveMessageRequest.class))).thenReturn(emptyMessageResponse);

        sqs.client = mockClient;

        List<Wmdata> returnedMessages = sqs.getMessages();
        assert(returnedMessages.isEmpty());
    }

    @Test
    public void getMessagesNotEmpty() {
        SqsClient mockClient = mock(SqsClient.class);

        Message message = Message.builder()
                .body(json)
                .build();

        ReceiveMessageResponse messages = ReceiveMessageResponse.builder()
                .messages(Collections.singletonList(message))
                .build();

        when(mockClient.receiveMessage(any(ReceiveMessageRequest.class)))
                .thenReturn(messages)
                .thenReturn(ReceiveMessageResponse.builder()
                    .messages(Collections.emptyList())
                    .build());

        sqs.client = mockClient;

        List<Wmdata> returnedMessages = sqs.getMessages();
        assert(!returnedMessages.isEmpty());
    }
}