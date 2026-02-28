package com.example.batch.service;

import com.example.batch.model.CustomerTarget;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
public class CustomerTargetClientService {

    private final RestTemplate restTemplate;

    // Replace this with the actual base URL of your target endpoint
    private static final String BASE_URL = "http://localhost:8080/api/customers";

    public CustomerTargetClientService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public List<CustomerTarget> getCustomersByEmail(String email) {
        String url = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                .queryParam("email", email)
                .toUriString();

        ResponseEntity<List<CustomerTarget>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<CustomerTarget>>() {
                });

        return response.getBody();
    }
}
