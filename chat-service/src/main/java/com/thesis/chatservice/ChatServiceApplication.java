package com.thesis.chatservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class ChatServiceApplication {

    @GetMapping("/api1")
    public String api1(){
        return "api1";
    }

    public static void main(String[] args) {
        SpringApplication.run(ChatServiceApplication.class, args);
    }

}
