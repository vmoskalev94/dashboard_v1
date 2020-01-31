package ru.homecredit.microservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Properties;

//import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
//@EnableDiscoveryClient
@EnableScheduling
@EnableCaching
public class Application {
    public static void main(String[] args) {
        Properties props = System.getProperties();
        props.put("http.proxyHost", "127.0.0.1");
        props.put("http.proxyPort", "3128");

        SpringApplication.run(Application.class, args);
    }
}