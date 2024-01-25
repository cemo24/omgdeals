package org.monzon.Wally;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqsMessenger {
    private static final Logger logger = LoggerFactory.getLogger(SqsMessenger.class);
    private static final ObjectMapper mapper = new ObjectMapper();
    private static SqsMessenger instance;
    private SqsClient client;
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

    public boolean sendMessage(String url, String message) {
        try {
            SendMessageRequest request = SendMessageRequest.builder()
                    .queueUrl(url)
                    .messageBody(message)
                    .build();
            client.sendMessage(request);
            return true;
        } catch (Exception e) {
            logger.error("Error Sending to SQS", e);
            return false;
        }
    }

    private String convertObjectToMessage(Wmdata obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            logger.error("Object to String Conversion Error", e);
            return "";
        }
    }

    public void sendBatchMessages(List<Wmdata> latestResults){
        for(var res: latestResults){
            instance.sendMessage(SQS_URL, instance.convertObjectToMessage(res));
        }
        logger.info("Message Batch Sent to SQS");
        latestResults.clear();
    }
}
