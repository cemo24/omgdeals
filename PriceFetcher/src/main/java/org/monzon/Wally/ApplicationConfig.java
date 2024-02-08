package org.monzon.Wally;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.*;

import java.util.concurrent.TimeUnit;

@Configuration
public class ApplicationConfig {

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
                .connectionPool(new ConnectionPool(30, 5, TimeUnit.SECONDS))
                //.proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxy.getIp(), proxy.getPort())))
                .build();
    }

    @Bean
    public Request.Builder requestBuilder() {
        return new Request.Builder().method("GET", null);
    }
}
