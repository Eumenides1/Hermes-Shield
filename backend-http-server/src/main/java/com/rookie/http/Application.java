package com.rookie.http;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author eumenides
 * @description
 * @date 2024/4/24
 */
@SpringBootApplication(scanBasePackages = "com.rookie")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}

