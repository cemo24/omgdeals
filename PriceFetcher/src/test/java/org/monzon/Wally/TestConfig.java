package org.monzon.Wally;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("test")
public class TestConfig {

    public TestConfig(){
        System.out.println("$$$$\nloading Test config/n$$$$");
    }

    @Autowired
    private OkHttpClient okHttpClient;

    @Autowired
    private Request.Builder requestBuilder;

    @Bean
    public TaskOkHttp taskOkHttp() {
        return new TaskOkHttp(okHttpClient, requestBuilder);
    }
}
