package org.example.binancejobservice.config;

import org.example.binancejobservice.interceptors.LoggingInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

@org.springframework.context.annotation.Configuration
public class ApiConfiguration {

    @Bean
    public RestClient response () {

        return RestClient.builder()
                .requestInterceptor(new LoggingInterceptor())
                .build();

    }

}

