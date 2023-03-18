package com.smarttrading.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaRepositories("com.smarttrading.app.database")
@EnableScheduling
public class SmartTradingApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartTradingApplication.class, args);
    }

}
