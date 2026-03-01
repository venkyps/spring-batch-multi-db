package com.example.batch.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class HostStatusResponseTest {

    @Test
    void testGetterAndSetter() {
        HostStatusResponse response = new HostStatusResponse();
        response.setHostStatus("success");

        assertEquals("success", response.getHostStatus());
    }

    @Test
    void testDefaultConstructor() {
        HostStatusResponse response = new HostStatusResponse();
        assertNull(response.getHostStatus());
    }

    @Test
    void testToString() {
        HostStatusResponse response = new HostStatusResponse();
        response.setHostStatus("pending");

        String result = response.toString();
        assertTrue(result.contains("pending"));
    }
}
