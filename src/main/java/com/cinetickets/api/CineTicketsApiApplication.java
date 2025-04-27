package com.cinetickets.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CineTicketsApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(CineTicketsApiApplication.class, args);
    }

}