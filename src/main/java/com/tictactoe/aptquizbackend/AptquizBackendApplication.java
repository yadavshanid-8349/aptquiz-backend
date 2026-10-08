package com.tictactoe.aptquizbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AptquizBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(
                AptquizBackendApplication.class,
                args
        );
    }
}