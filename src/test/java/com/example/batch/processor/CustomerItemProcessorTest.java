package com.example.batch.processor;

import com.example.batch.model.CustomerSource;
import com.example.batch.model.CustomerTarget;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CustomerItemProcessorTest {

    private final CustomerItemProcessor processor = new CustomerItemProcessor();

    @Test
    void testProcess() throws Exception {
        // Arrange
        CustomerSource source = new CustomerSource();
        // Assuming CustomerSource.id is now a UUID
        java.util.UUID sourceId = java.util.UUID.randomUUID();
        source.setId(sourceId);
        source.setStatus("PENDING");
        source.setRefNo("REF-001");

        // Act
        CustomerTarget target = processor.process(source);

        // Assert
        assertNotNull(target);
        // Since your processor generates a NEW randomUUID for the target,
        // we check that it exists and is valid.
        assertNotNull(target.getId());
        assertTrue(target.getId() instanceof java.util.UUID);

        assertEquals("REF-001", target.getRefNo());
        assertEquals("PENDING", target.getStatus());

        // Note: Ensure your CustomerTarget model has a 'createdAt' field
        // or that your processor sets it, otherwise this line will fail.
        assertNotNull(target.getCreatedAt());
    }
}
