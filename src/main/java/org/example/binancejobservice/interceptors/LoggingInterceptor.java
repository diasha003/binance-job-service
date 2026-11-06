package org.example.binancejobservice.interceptors;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.util.logging.Logger;

public class LoggingInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger log = Logger.getLogger(LoggingInterceptor.class.getName());

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        long startTime = System.currentTimeMillis();

        log.info("HTTP Request: " + request.getMethod() + " " + request.getURI());

        ClientHttpResponse response = execution.execute(request, body);

        long duration = System.currentTimeMillis() - startTime;
        log.info("HTTP Response: status = " + response.getStatusCode() + ", duration = " + duration + "ms");

        return response;
    }
}
