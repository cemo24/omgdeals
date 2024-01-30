package org.monzon.Main;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqsMessenger {
    private static final Logger logger = LoggerFactory.getLogger(SqsMessenger.class);
    private static final ObjectMapper mapper = new ObjectMapper();
    private static SqsMessenger instance;
    public SqsClient client;
    private static String SQS_URL;

    private SqsMessenger() {
        client = SqsClient.builder()
                .region(Region.US_EAST_2)
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .build();

        SQS_URL = System.getenv("SQS_URL");
    }

    public static SqsMessenger getInstance() {
        if (instance == null) {
            synchronized (SqsMessenger.class) {
                if (instance == null) {
                    instance = new SqsMessenger();
                    logger.info("SQS Client Initialized");
                }
            }
        }
        return instance;
    }

    public List<Wmdata> getMessages() {

        List<Wmdata> receivedMessages = new ArrayList<>();
        logger.info("SQS - Getting Messages");
        while (true) {
            ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
                    .queueUrl(SQS_URL)
                    .maxNumberOfMessages(10)
                    .build();

            List<Message> messages = client.receiveMessage(receiveRequest).messages();

            if (messages.isEmpty()) {
                break;
            }

            for (Message message : messages) {
                Wmdata messageObject;

                logger.info("message body:");
                logger.info(message.body());

                try {
                    messageObject = mapper.readValue(message.body(), Wmdata.class);
                } catch (JsonProcessingException e) {
                    logger.error("Error Mapping From SQS", e);
                    continue;
                }
                receivedMessages.add(messageObject);
            }
        }
        return receivedMessages;
    }
}
