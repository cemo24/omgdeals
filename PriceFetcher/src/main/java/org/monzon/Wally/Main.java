package org.monzon.Wally;

import net.razorvine.pickle.Unpickler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

@SpringBootApplication
public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    @Autowired
    private SqsMessenger sqs;

    @Autowired
    private TaskOkHttp tasks;

    @Autowired
    public Main(TaskOkHttp tasks, SqsMessenger sqs) {
        this.tasks = tasks;
        this.sqs = sqs;
    }
    public static void main(String[] args){
        SpringApplication.run(Main.class, args);
    }

    @PostConstruct
    public void run() throws IOException, InterruptedException {

        List<Wmdata> latestResults = new ArrayList<>();

        int proxyIndex = 0, pxIndex = 0, upcIndex = 0, wmheadersIndex = 0, seatCounter = 0;

        Unpickler unpickler = new Unpickler();
        FileUtils utils = new FileUtils(unpickler);

        Object unpickledUpcs = utils.unpickleFile(System.getenv("UPC_PATH"));

        if (!(unpickledUpcs instanceof HashMap)) {
            logger.error("Cannot unpickle UPCs");
            throw new IOException("Cannot unpickle UPCs");
        }

        HashMap<String, Double> unfilteredUpcs = (HashMap<String, Double>) unpickledUpcs;
        HashMap<String, Double> upcs = getFilteredUpcs(unfilteredUpcs);

        //******* FOR TESTING REMOVE AFTER ********
        upcs = new HashMap<String, Double>() {{put("710425671272", 200.00);}};
        //******* FOR TESTING REMOVE AFTER ********

        TreeMap<String, Double> sortedUpcs = new TreeMap<>(upcs);


        int totalStores = RequestParams.STORES.size(), totalUpcs = upcs.size();
        logger.info(String.format("Total UPCs: %d Total Stores: %d", totalUpcs, totalStores));

        ExecutorService airplane = Executors.newFixedThreadPool(Config.MAX_THREADS);
        List<TaskOkHttp> taskList = new ArrayList<>();

        for (Map.Entry<String, Double> upc : sortedUpcs.entrySet()) {

            int storeIndex = 0;
            upcIndex++;
            logger.info(String.format("At UPC# %d", upcIndex));

            for (String store : RequestParams.STORES) {

                storeIndex++; seatCounter++; proxyIndex++; wmheadersIndex++;

                //get this iterations request spec
                ProxyCreds proxyIt = RequestParams.PROXIES.get(proxyIndex % RequestParams.PROXIES.size());
                String pxIt = RequestParams.PX.get(proxyIndex % RequestParams.PX.size());
                var wmheadersIt = RequestParams.getReqHeaders().get(wmheadersIndex % RequestParams.getReqHeaders().size());

                try {
                    TaskOkHttp thisTask = tasks.createTaskOkHttp();
                    thisTask.setUpc(upc.getKey());
                    thisTask.setListPrice(upc.getValue());
                    thisTask.setStore(store);
                    thisTask.setPx_header(pxIt);
                    thisTask.setProxy(proxyIt);
                    thisTask.setWm_headers(wmheadersIt);

                    taskList.add(thisTask);
                } catch (Exception e) {
                    logger.error("Error Processing taskList", e);
                    continue;
                }

                if (seatCounter >= Config.MAX_THREADS || (storeIndex == totalStores && upcIndex == totalUpcs)) {
                    proxyIndex = ++proxyIndex % RequestParams.PROXIES.size();
                    pxIndex = ++pxIndex % RequestParams.PX.size();

                    logger.info(String.format("invoking batch %s %s %s", store, upc.getKey(), RequestParams.PROXIES.get(proxyIndex).getIp()));
                    List<Future<Wmdata>> results = airplane.invokeAll(taskList);

                    for (Future<Wmdata> resultTask : results) {

                        try{
                            Wmdata result = resultTask.get();

                            if (result.getStorePrice() == null) {
                                continue;
                            }

                            if (resultFilterCheck(result)) {
                                continue;
                            }
                            latestResults.add(result);

                        } catch(Exception e){
                            logger.error("Error Processing Task", e);
                        }
                    }
                    seatCounter = 0;

                    taskList.clear();
                    airplane.shutdownNow();
                    airplane = Executors.newFixedThreadPool(Config.MAX_THREADS);

                    if(latestResults.size() > 0){
                        sqs.sendBatchMessages(latestResults); //sent and cleared
                    }
                }
                TimeUnit.SECONDS.sleep(Config.SLEEP_TIME_SEC);
            }
        }
        airplane.shutdownNow();
        try {
            if (!airplane.awaitTermination(Config.TIMEOUT_WAIT_SEC, TimeUnit.SECONDS)) {
                logger.info("Threads Not Complete");
            }
        } catch (InterruptedException e) {
            logger.warn("Threads Interrupted", e);
        }
        logger.info("Finished Fetching Prices");
    }


    private static boolean upcFilterCheck(String key, Double value){
        //return True if upc should be filtered out
        if(value < Config.MIN_LIST_PRICE){
            return true;
        }
        if(key.length() > 12){ //skip invalid keys
            return true;
        }
        return false;
    }

    private static HashMap<String, Double> getFilteredUpcs(HashMap<String, Double> upcs){
        Set<String> allKeys = new HashSet<>(upcs.keySet());

        //remove due to condition not being met
        for(String key : allKeys){
            if(upcFilterCheck(key, upcs.get(key))){
                upcs.remove(key);
            }
        }
        //remove due to being on ignore list
        for (String key : RequestParams.UPCS_TO_IGNORE) {
            upcs.remove(key);
        }
        return upcs;
    }

    private static boolean resultFilterCheck(Wmdata result){
        //True means should be filtered out
        Double listPrice = result.getListPrice();
        Double storePrice = result.getStorePrice();

        //******* FOR TESTING REMOVE AFTER ********
        if(storePrice != null){
            return false;
            //******* FOR TESTING REMOVE AFTER ********



//        // missing return price
//        if(storePrice == null || storePrice == 0.0){
//            return true;
//        }
//        // 50% clearance or better
//        if(storePrice > listPrice){
//            if(storePrice * 2 > listPrice){
//                return true;
        }
        return false;
    }
}
