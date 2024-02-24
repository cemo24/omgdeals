package org.monzon.Wally;

import net.razorvine.pickle.Unpickler;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.*;

import java.util.concurrent.TimeUnit;

@Configuration
@Profile("default")
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
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public Request.Builder requestBuilder() {
        return new Request.Builder().method("GET", null);
    }

    @Bean
    public Unpickler unpickler(){
        return new Unpickler();
    }

    @Bean
    public SqsMessenger sqs(){
        return new SqsMessenger();
    }
}
