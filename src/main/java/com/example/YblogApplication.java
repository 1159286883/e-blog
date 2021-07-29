package com.example;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@MapperScan({"com.example.mapper","com.example.commom"})
public class YblogApplication {

    public static void main(String[] args) {
        SpringApplication.run(YblogApplication.class, args);
        System.out.println("http://localhost:8080");
    }

}
