package org.monzon.Main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        SqsMessenger sqsClient = SqsMessenger.getInstance();
        DynamoClient dynamoClient = DynamoClient.getInstance();

        List<Wmdata> sqsMessages = sqsClient.getMessages();

        if(sqsMessages.isEmpty()){
            logger.info("SQS Empty");
            return;
        }

        List<Wmdata> filteredMessages = dynamoClient.filterMessages(sqsMessages);

        if(!filteredMessages.isEmpty()){

            for (int i = 0; i < filteredMessages.size(); i += 25) { //batchWrite limit
                int endIndex = Math.min(i + 25, filteredMessages.size());
                List<Wmdata> currentBatch = filteredMessages.subList(i, endIndex);
                dynamoClient.putItems(currentBatch);
            }

        }
    }
}
