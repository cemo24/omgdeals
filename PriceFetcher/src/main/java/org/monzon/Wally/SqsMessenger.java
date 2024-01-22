package org.monzon.Wally;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.util.List;
import java.util.logging.Logger;

public class SqsMessenger {

    private static final Logger logger = Logger.getLogger(SqsMessenger.class.getName());
    private static final ObjectMapper mapper = new ObjectMapper();
    private static SqsMessenger instance;
    private SqsClient client;

    private SqsMessenger() {
        client = SqsClient.builder()
                .region(Region.US_EAST_2)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
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
            logger.log(java.util.logging.Level.SEVERE, "Error Sending to SQS", e);
            return false;
        }
    }

    private String convertObjectToMessage(Wmdata obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            logger.log(java.util.logging.Level.SEVERE, "Object to String Conversion Error", e);
            return "";
        }
    }

    public void sendBatchMessages(List<Wmdata> latestResults){
        for(var res: latestResults){
            instance.sendMessage(Config.SQS_URL, instance.convertObjectToMessage(res));
        }
        logger.info("Message Batch Sent to SQS");
        latestResults.clear();
    }
}
