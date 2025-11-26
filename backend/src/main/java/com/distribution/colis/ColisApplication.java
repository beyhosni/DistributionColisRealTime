package com.distribution.colis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class ColisApplication {

    public static void main(String[] args) {
        SpringApplication.run(ColisApplication.class, args);
    }
}
