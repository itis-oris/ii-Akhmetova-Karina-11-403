package org.example.cakeshop;

import org.example.cakeshop.config.YandexMapsProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
@EnableConfigurationProperties(YandexMapsProperties.class)
public class CakeshopApplication {

    public static void main(String[] args) {
        SpringApplication.run(CakeshopApplication.class, args);
    }
}
