package org.monzon.Wally;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Main {
    public static void main(String[] args) throws Exception{
        ConfigurableApplicationContext context = SpringApplication.run(Main.class, args);
        context.getBean(Runner.class).run();
    }
}