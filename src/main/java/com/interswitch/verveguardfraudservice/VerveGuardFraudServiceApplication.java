package com.interswitch.verveguardfraudservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class VerveGuardFraudServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(VerveGuardFraudServiceApplication.class, args);
    }

}
