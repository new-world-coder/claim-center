package com.claimcenter.claim;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.claimcenter")
public class ClaimServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ClaimServiceApplication.class, args);
    }
}
