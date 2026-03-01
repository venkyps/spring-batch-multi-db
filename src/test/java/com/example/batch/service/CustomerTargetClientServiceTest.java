package com.example.batch.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.example.batch.model.HostStatusResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class CustomerTargetClientServiceTest {

    @Mock
    private RestTemplateBuilder restTemplateBuilder;

    @Mock
    private RestTemplate restTemplate;

    private CustomerTargetClientService clientService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(restTemplateBuilder.build()).thenReturn(restTemplate);
        clientService = new CustomerTargetClientService(restTemplateBuilder);
    }

    @Test
    void testGetHostStatusByRefNo_returnsSuccessStatus() {
        // Arrange
        HostStatusResponse expectedResponse = new HostStatusResponse();
        expectedResponse.setHostStatus("success");

        when(restTemplate.getForEntity(anyString(), eq(HostStatusResponse.class)))
                .thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

        // Act
        HostStatusResponse result = clientService.getHostStatusByRefNo("REF-001");

        // Assert
        assertNotNull(result);
        assertEquals("success", result.getHostStatus());
        verify(restTemplate).getForEntity(contains("referenceNumber=REF-001"), eq(HostStatusResponse.class));
    }

    @Test
    void testGetHostStatusByRefNo_returnsFailedStatus() {
        // Arrange
        HostStatusResponse expectedResponse = new HostStatusResponse();
        expectedResponse.setHostStatus("failed");

        when(restTemplate.getForEntity(anyString(), eq(HostStatusResponse.class)))
                .thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

        // Act
        HostStatusResponse result = clientService.getHostStatusByRefNo("REF-002");

        // Assert
        assertNotNull(result);
        assertEquals("failed", result.getHostStatus());
    }

    @Test
    void testGetHostStatusByRefNo_returnsNullBody() {
        // Arrange
        when(restTemplate.getForEntity(anyString(), eq(HostStatusResponse.class)))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.OK));

        // Act
        HostStatusResponse result = clientService.getHostStatusByRefNo("REF-003");

        // Assert
        assertNull(result);
    }

    @Test
    void testGetHostStatusByRefNo_throwsException() {
        // Arrange
        when(restTemplate.getForEntity(anyString(), eq(HostStatusResponse.class)))
                .thenThrow(new RuntimeException("Connection refused"));

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> clientService.getHostStatusByRefNo("REF-004"));
    }
}
