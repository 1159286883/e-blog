package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class YblogApplication {

    public static void main(String[] args) {
        SpringApplication.run(YblogApplication.class, args);
        System.out.println("http://localhost:8080");
    }

}
