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
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("singleton")
public class SqsMessenger {
    private static final Logger logger = LoggerFactory.getLogger(SqsMessenger.class);
    private static final ObjectMapper mapper = new ObjectMapper();
    private static String SQS_URL;

    private final SqsClient client;

    public SqsMessenger() {
        client = SqsClient.builder()
                .region(Region.US_EAST_2)
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .build();

        SQS_URL = System.getenv("SQS_URL");
        logger.info("SQS Client Initialized");
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
            sendMessage(SQS_URL, convertObjectToMessage(res));
        }
        logger.info("Message Batch Sent to SQS");
        latestResults.clear();
    }
}
