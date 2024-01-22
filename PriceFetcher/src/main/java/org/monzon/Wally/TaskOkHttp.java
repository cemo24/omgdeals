package org.monzon.Wally;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.net.http.*;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import java.util.zip.ZipException;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import lombok.AllArgsConstructor;
import lombok.Getter;
import okhttp3.*;


@AllArgsConstructor
@Getter
public class TaskOkHttp implements Callable<Wmdata>
{
    private final String upc;
    private final Double listPrice;
    private final String store;
    private final String px_header;
    private final ProxyCreds proxy;
    private final Map<String, String> wm_headers;

    private final Logger logger = Logger.getLogger(TaskOkHttp.class.getName());

    @Override
    public Wmdata call() throws Exception
    {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectionPool(new ConnectionPool(30, 5, TimeUnit.SECONDS))
                //.proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxy.getIp(), proxy.getPort())))
                .build();

//        String refId= UUID.randomUUID().toString()
//        String deviceId = wm_headers.get("x-o-platform-version");

        String url = RequestParams.getUrl(upc);

        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        var request = new Request.Builder()
                .url(url)
                .method("GET", null);

        wm_headers.remove("wm_instore_id");
        wm_headers.put("wm_instore_id", store);

        for (Map.Entry<String, String> entry : wm_headers.entrySet()) {
            if(entry.getKey().equals("wm_instore_id")){
                request.addHeader(entry.getKey(), store);
                continue;
            }
            if(entry.getKey().equals("url")){continue;}
            request.addHeader(entry.getKey(), entry.getValue());
        }

        Request rep2 = request.build();

        String wow;
        Response response;

        try{
            response = client.newCall(rep2).execute();
        }catch(HttpTimeoutException e){
            logger.log(java.util.logging.Level.SEVERE, String.format("Timeout exception: %s", proxy.getIp()), e);
            return new Wmdata(null, null, 0, null, null, 0);
        }

        GZIPInputStream gzip;
        try{
            gzip = new GZIPInputStream(new ByteArrayInputStream(response.body().bytes()));//response.getBody()));
        }catch(ZipException e){
            logger.log(java.util.logging.Level.SEVERE, String.format("GZIP Error: %s", proxy.getIp()), e);
            throw new ZipException();
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(gzip));
        wow = br.readLine();

        response.close();

        if(wow == null){
            logger.info("Response Body Empty");
            return new Wmdata(null, null, 0, null, null, 0);
        }


        JsonObject jsonElement;
        try{
            jsonElement = JsonParser.parseString(wow).getAsJsonObject();
        }catch(IllegalStateException e){
            logger.log(java.util.logging.Level.SEVERE, String.format("Response Json Parse Error", proxy.getIp()), e);
            throw new IllegalStateException("Error not able to parse response as json");
        }
        JsonArray items = jsonElement.getAsJsonObject("data").getAsJsonObject("contentLayout").getAsJsonArray("modules");


        for(JsonElement item : items){

            Optional<JsonArray> allOffers = Optional.ofNullable(item)
                    .filter(e -> !e.isJsonNull())
                    .map(e -> e.getAsJsonObject())
                    .filter(e -> !e.isJsonNull())
                    .map(e -> e.get("configs"))
                    .filter(e -> !e.isJsonNull())
                    .map(e -> e.getAsJsonObject().get("product"))
                    .filter(e -> !e.isJsonNull())
                    .map(e -> e.getAsJsonObject().get("allOffers"))
                    .filter(e -> !e.isJsonNull())
                    .map(e -> e.getAsJsonArray());


            if(allOffers.isEmpty()){
                continue;
            }

            Double store_price = 0.0, current_price = 0.0;

            for(JsonElement onlyOffer  : allOffers.get()) {

                Optional<JsonObject> offerObject = Optional.ofNullable(onlyOffer)
                        .filter(e -> !e.isJsonNull())
                        .map(e -> e.getAsJsonObject());

                if(offerObject.isEmpty()){
                    continue;
                }
                JsonObject offer = offerObject.get();

                Optional<String> internalSellerCheck = Optional.ofNullable(offer.get("sellerType"))
                        .filter(e -> !e.isJsonNull())
                        .filter(e -> e.isJsonPrimitive())
                        .map(e -> e.getAsJsonPrimitive().getAsString())
                        .filter(e -> e.equals("INTERNAL"));

                if(internalSellerCheck.isEmpty()){
                    continue;
                }

                var storePriceElementCheck = Optional.ofNullable(offer.get("priceInfo"))
                        .filter(e -> !e.isJsonNull())
                        .map(e -> e.getAsJsonObject());

                if(storePriceElementCheck.isEmpty()){
                    continue;
                }
                JsonObject store_price_element_a = storePriceElementCheck.get(); //add isjsonnull check

                if(store_price_element_a.isJsonNull()){
                    continue;
                }


                if (store_price_element_a.has("storePrice") && !store_price_element_a.get("storePrice").isJsonNull()) {
                    JsonPrimitive store_price_element = store_price_element_a.getAsJsonObject("storePrice").getAsJsonObject("currentPrice").getAsJsonPrimitive("priceString");
                    String store_price_string = store_price_element.getAsString();

                    if (store_price_string.equals("See price in cart")) {
                        Double store_price_element_double = store_price_element_a.getAsJsonObject("storePrice").getAsJsonObject("currentPrice").getAsJsonPrimitive("price").getAsDouble();
                        store_price_string = String.valueOf(store_price_element_double);
                    }

                    store_price = Double.parseDouble(store_price_string.replace("$", "").replace(",", ""));
                }
                if (store_price == 0.0) {
                    continue;
                }

                JsonElement availabilityStatusMember = onlyOffer.getAsJsonObject().get("availabilityStatus");
                if(availabilityStatusMember.isJsonNull() || !availabilityStatusMember.isJsonPrimitive()){
                    continue;
                }

                //JsonPrimitive availability_element = onlyOffer.getAsJsonObject().getAsJsonPrimitive("availabilityStatus");
                JsonPrimitive availability_element = availabilityStatusMember.getAsJsonPrimitive();
                String availability = availability_element.getAsString();

                if (store_price_element_a.has("currentPrice") && !store_price_element_a.get("currentPrice").isJsonNull()) {

                    JsonElement priceStringObject = store_price_element_a.getAsJsonObject("currentPrice").get("priceString");
                    String store_price_string = null;
                    JsonPrimitive store_price_element = null;

                    if(!priceStringObject.isJsonNull()){
                        store_price_element = store_price_element_a.getAsJsonObject("currentPrice").getAsJsonPrimitive("priceString");
                        store_price_string = store_price_element.getAsString();
                    }


                    if (store_price_string.equals("See price in cart") || store_price_element==null) {

                        if(store_price_element_a.getAsJsonObject("currentPrice").get("price").isJsonNull()){
                            continue; //no 2price anywhere so next item
                        }

                        Double store_price_element_double = store_price_element_a.getAsJsonObject("currentPrice").getAsJsonPrimitive("price").getAsDouble();
                        store_price_string = String.valueOf(store_price_element_double);
                    }
                    current_price = Double.parseDouble(store_price_string.replace("$", "").replace(",", ""));
                }

                Double lowestStorePrice = 0.0;

                if (current_price == 0 || store_price == 0) {
                    lowestStorePrice = Math.max(current_price, store_price);
                } else {
                    lowestStorePrice = Math.min(current_price, store_price);
                }
                logger.info(String.format("%s %f %f %s %s", upc, listPrice, lowestStorePrice, availability, store));

                int stock = 0;
                if (!availability.equals("OUT_OF_STOCK")) {
                    stock = 1;
                }
                return new Wmdata(upc, store, stock, listPrice, lowestStorePrice, Instant.now().getEpochSecond());
            }
        }
        return new Wmdata(null, null, 0, null, null, 0);
    }
}
