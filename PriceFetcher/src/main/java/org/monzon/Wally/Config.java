package org.monzon.Wally;

public class Config {
    final static Double MIN_LIST_PRICE = 49.00;
    final static Integer MAX_THREADS = 2;
    final static String SQS_URL = "https://sqs.us-east-2.amazonaws.com/966778578332/pricing";
    final static Integer TIMEOUT_WAIT_SEC = 15;
    final static String UPC_PATH = "upc_price.pickle";
    final static Integer SLEEP_TIME_SEC = 10;
}
