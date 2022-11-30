package com.example.demonats;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class DemoNatsApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoNatsApplication.class, args);
    }

}
