package org.monzon.Wally;

import com.google.gson.*;
import lombok.Setter;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.net.http.HttpTimeoutException;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipException;

@Component
@Setter
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TaskOkHttp implements Callable<Wmdata>{

    private OkHttpClient okHttpClient;
    private Request.Builder requestBuilder;

    private final String retailer = "WM";
    private String upc;
    private Double listPrice;
    private String store;
    private String px_header;
    private ProxyCreds proxy;
    private Map<String, String> wm_headers;

    private static final Logger logger = LoggerFactory.getLogger(TaskOkHttp.class);
    public TaskOkHttp createTaskOkHttp() {
        return new TaskOkHttp(okHttpClient, requestBuilder);
    }

    @Autowired
    public TaskOkHttp(OkHttpClient okHttpClient, Request.Builder requestBuilder) {
        this.okHttpClient = okHttpClient;
        this.requestBuilder = requestBuilder;
    }

    @Override
    public Wmdata call() throws Exception {

        requestBuilder.addHeader("wm_instore_id", store);

        for (Map.Entry<String, String> entry : wm_headers.entrySet()) {

            if (entry.getKey().equals("url")) {
                continue;
            }
            requestBuilder.addHeader(entry.getKey(), entry.getValue());
        }
        String url = RequestParams.getUrl(upc);

        requestBuilder.url(url);

        Request request = requestBuilder.build();

        String wow;
        Response response;

        try {
            Call requestCall = okHttpClient.newCall(request);
            response = requestCall.execute();
        } catch (HttpTimeoutException e) {
            logger.error(String.format("Timeout exception: %s", proxy.getIp()), e);
            return new Wmdata();
        }

        GZIPInputStream gzip;
        try {
            gzip = new GZIPInputStream(new ByteArrayInputStream(response.body().bytes()));

        } catch (ZipException e) {
            logger.error(String.format("GZIP Error: %s", proxy.getIp()), e);
            throw new ZipException();
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(gzip));
        wow = br.readLine();

        response.close();









        if (wow == null) {
            logger.info("Response Body Empty");
            return new Wmdata();
        }

        JsonObject jsonElement;
        try {
            jsonElement = JsonParser.parseString(wow).getAsJsonObject();
        } catch (IllegalStateException e) {
            logger.error(String.format("Response Json Parse Error", proxy.getIp()), e);
            throw new IllegalStateException("Error not able to parse response as json");
        }
        JsonArray items = jsonElement.getAsJsonObject("data").getAsJsonObject("contentLayout").getAsJsonArray("modules");

        for (JsonElement item : items) {

            Optional<JsonObject> productTree = Optional.ofNullable(item)
                    .filter(e -> !e.isJsonNull())
                    .map(e -> e.getAsJsonObject())
                    .filter(e -> !e.isJsonNull())
                    .map(e -> e.get("configs"))
                    .filter(e -> !e.isJsonNull())
                    .map(e -> e.getAsJsonObject().get("product"))
                    .filter(e -> !e.isJsonNull())
                    .map(e ->e.getAsJsonObject());



            Optional<String> nameTree = Optional.ofNullable(productTree.get().get("name"))
                    .filter(e -> !e.isJsonNull())
                    .filter(e -> e.isJsonPrimitive())
                    .map(e -> e.getAsJsonPrimitive().getAsString());


            Optional<String> imageTree = Optional.ofNullable(productTree.get().get("imageInfo"))
                    .filter(e -> !e.isJsonNull())
                    .map(e -> e.getAsJsonObject().get("thumbnailUrl"))
                    .filter(e -> !e.isJsonNull())
                    .filter(e -> e.isJsonPrimitive())
                    .map(e -> e.getAsJsonPrimitive().getAsString());

            String title = "";
            String image = "";
            if(!nameTree.isEmpty()){
                title = nameTree.get();
            }
            if(!imageTree.isEmpty()){
                image = imageTree.get();
            }

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


            if (allOffers.isEmpty()) {
                continue;
            }

            Double store_price = 0.0, current_price = 0.0;

            for (JsonElement onlyOffer : allOffers.get()) {

                Optional<JsonObject> offerObject = Optional.ofNullable(onlyOffer)
                        .filter(e -> !e.isJsonNull())
                        .map(e -> e.getAsJsonObject());

                if (offerObject.isEmpty()) {
                    continue;
                }
                JsonObject offer = offerObject.get();

                Optional<String> internalSellerCheck = Optional.ofNullable(offer.get("sellerType"))
                        .filter(e -> !e.isJsonNull())
                        .filter(e -> e.isJsonPrimitive())
                        .map(e -> e.getAsJsonPrimitive().getAsString())
                        .filter(e -> e.equals("INTERNAL"));

                if (internalSellerCheck.isEmpty()) {
                    continue;
                }

                var storePriceElementCheck = Optional.ofNullable(offer.get("priceInfo"))
                        .filter(e -> !e.isJsonNull())
                        .map(e -> e.getAsJsonObject());

                if (storePriceElementCheck.isEmpty()) {
                    continue;
                }
                JsonObject store_price_element_a = storePriceElementCheck.get(); //add isjsonnull check

                if (store_price_element_a.isJsonNull()) {
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
                if (availabilityStatusMember.isJsonNull() || !availabilityStatusMember.isJsonPrimitive()) {
                    continue;
                }

                JsonPrimitive availability_element = availabilityStatusMember.getAsJsonPrimitive();
                String availability = availability_element.getAsString();

                if (store_price_element_a.has("currentPrice") && !store_price_element_a.get("currentPrice").isJsonNull()) {

                    JsonElement priceStringObject = store_price_element_a.getAsJsonObject("currentPrice").get("priceString");
                    String store_price_string = null;
                    JsonPrimitive store_price_element = null;

                    if (!priceStringObject.isJsonNull()) {
                        store_price_element = store_price_element_a.getAsJsonObject("currentPrice").getAsJsonPrimitive("priceString");
                        store_price_string = store_price_element.getAsString();
                    }

                    if (store_price_string.equals("See price in cart") || store_price_element == null) {

                        if (store_price_element_a.getAsJsonObject("currentPrice").get("price").isJsonNull()) {
                            continue;
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
                String key = upc + "_" + store + "_" + retailer;
                return new Wmdata(key, upc, store, retailer, stock, listPrice, lowestStorePrice, Instant.now().getEpochSecond(), title, image);
            }
        }
        return new Wmdata();
    }

}
