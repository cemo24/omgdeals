package org.monzon.Wally;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.zip.ZipException;

@Component
public class Runner{

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private final SqsMessenger sqs;

    private final TaskOkHttp tasks;

    private final FileUtils utils;

    private final ApplicationContext context;

    @Autowired
    public Runner(SqsMessenger sqs, TaskOkHttp tasks, FileUtils utils, ApplicationContext context) {
        this.sqs = sqs;
        this.tasks = tasks;
        this.utils = utils;
        this.context = context;
    }

    public void run() throws Exception{

        List<Wmdata> latestResults = new ArrayList<>();

        int proxyIndex = 0, pxIndex = 0, upcIndex = 0, wmheadersIndex = 0, seatCounter = 0;

        Object unpickledUpcs = utils.unpickleFile();

        if (!(unpickledUpcs instanceof HashMap)) {
            logger.error("Cannot unpickle UPCs");
            throw new IOException("Cannot unpickle UPCs");
        }

        HashMap<String, Double> unfilteredUpcs = (HashMap<String, Double>) unpickledUpcs;
        HashMap<String, Double> upcs = getFilteredUpcs(unfilteredUpcs);
        TreeMap<String, Double> sortedUpcs = new TreeMap<>(upcs);

        int totalStores = RequestParams.STORES.size(), totalUpcs = upcs.size();
        logger.info(String.format("Total UPCs: %d Total Stores: %d", totalUpcs, totalStores));

        ExecutorService airplane = Executors.newFixedThreadPool(Config.MAX_THREADS);
        List<TaskOkHttp> taskList = new ArrayList<>();

        var totalHeaders = RequestParams.getReqHeaders();

        for (Map.Entry<String, Double> upc : sortedUpcs.entrySet()) {

            int storeIndex = 0;
            upcIndex++;

            logger.info(String.format("At UPC# %d", upcIndex));

            for (String store : RequestParams.STORES) {

                storeIndex++; seatCounter++; proxyIndex++; wmheadersIndex++;

                ProxyCreds proxyIt = RequestParams.PROXIES.get(proxyIndex % RequestParams.PROXIES.size());
                String pxIt = RequestParams.PX.get(proxyIndex % RequestParams.PX.size());
                var wmheadersIt = totalHeaders.get(wmheadersIndex % totalHeaders.size());

                try {
                    TaskOkHttp thisTask = context.getBean(TaskOkHttp.class);

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

                    int taskCounter = -1;

                    for (Future<Wmdata> resultTask : results) {
                        taskCounter++;
                        try{
                            Wmdata result = resultTask.get();

                            if (result.getStorePrice() == null || result.getStorePrice() <= 0.0) {
                                continue;
                            }

                            if (resultFilterCheck(result)) {
                                continue;
                            }
                            latestResults.add(result);

                        } catch(Exception e) {

                            logger.error("Error Processing Task", e);

                            if (e instanceof ExecutionException && e.getCause() instanceof ZipException) {

                                TaskOkHttp originalTask = taskList.get(taskCounter);
                                Map<String, String> staleHeaders = originalTask.wm_headers;

                                for(int i = 0; i < totalHeaders.size(); i++){
                                    if(totalHeaders.get(i) == staleHeaders){
                                        totalHeaders.remove(i);
                                        logger.error("Removed Stale Headers");
                                        if (totalHeaders.size() == 0){
                                            throw new IllegalArgumentException("All Headers Are Stale");
                                        }
                                        break;
                                    }
                                }
                            }

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

        // missing return price & price check
        if(storePrice == null || storePrice == 0.0 || storePrice * 2 > listPrice){
            return true;
        }
        return false;
    }
}
