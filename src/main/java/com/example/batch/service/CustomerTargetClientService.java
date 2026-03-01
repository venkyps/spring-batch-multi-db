package com.example.batch.service;

import com.example.batch.model.HostStatusResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class CustomerTargetClientService {

    private static final Logger log = LoggerFactory.getLogger(CustomerTargetClientService.class);
    private final RestTemplate restTemplate;

    // Replace this with the actual base URL of your target endpoint
    private static final String BASE_URL = "http://localhost:8082/api/records";

    public CustomerTargetClientService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    /**
     * Calls the REST endpoint to retrieve the host_status for a given refNo.
     * Deserializes the JSON response into a HostStatusResponse object
     * and returns the hostStatus field.
     */
    public HostStatusResponse getHostStatusByRefNo(String refNo) {
        String url = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                .queryParam("referenceNumber", refNo)
                .toUriString();

        log.info("Calling REST endpoint for host_status: {}", url);
        ResponseEntity<HostStatusResponse> response = restTemplate.getForEntity(url, HostStatusResponse.class);
        HostStatusResponse body = response.getBody();

        String hostStatus = (body != null) ? body.getHostStatus() : null;
        log.info("Parsed hostStatus='{}' from response for refNo={}", hostStatus, refNo);
        return body;
    }
}
