package com.example.batch.listener;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.example.batch.model.HostStatusResponse;
import com.example.batch.service.CustomerTargetClientService;
import java.util.*;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.batch.item.Chunk;
import org.springframework.jdbc.core.JdbcTemplate;

public class CustomerItemWriteListenerTest {

    @Mock
    private CustomerTargetClientService clientService;

    @Mock
    private DataSource targetDataSource;

    @Mock
    private JdbcTemplate jdbcTemplate;

    private CustomerItemWriteListener listener;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // We create the listener, but we'll inject a mock JdbcTemplate via reflection
        listener = new CustomerItemWriteListener(clientService, targetDataSource);
        // Replace the internal jdbcTemplate with our mock
        org.springframework.test.util.ReflectionTestUtils.setField(listener, "jdbcTemplate", jdbcTemplate);
    }

    @Test
    void testAfterWrite_shouldCallRestAndUpdateForNonSuccessRows() {
        // Arrange: simulate DB returning 2 non-success rows
        UUID id1 = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID id2 = UUID.fromString("22222222-2222-2222-2222-222222222222");

        Map<String, Object> row1 = new HashMap<>();
        row1.put("id", id1);
        row1.put("ref_no", "REF-001");
        row1.put("host_status", null);

        Map<String, Object> row2 = new HashMap<>();
        row2.put("id", id2);
        row2.put("ref_no", "REF-002");
        row2.put("host_status", "pending");

        when(jdbcTemplate.queryForList(anyString())).thenReturn(List.of(row1, row2));

        HostStatusResponse response1 = new HostStatusResponse();
        response1.setHostStatus("success");
        when(clientService.getHostStatusByRefNo("REF-001")).thenReturn(response1);

        HostStatusResponse response2 = new HostStatusResponse();
        response2.setHostStatus("failed");
        when(clientService.getHostStatusByRefNo("REF-002")).thenReturn(response2);

        when(jdbcTemplate.update(anyString(), any(), any())).thenReturn(1);

        // Act
        Chunk<String> dummyChunk = new Chunk<>(List.of("item1"));
        listener.afterWrite(dummyChunk);

        // Assert: REST was called for both rows
        verify(clientService).getHostStatusByRefNo("REF-001");
        verify(clientService).getHostStatusByRefNo("REF-002");

        // Assert: DB update was called for both rows
        verify(jdbcTemplate).update(
                eq("UPDATE target_schema.customer SET host_status = ?,target_timestamp = CURRENT_TIMESTAMP WHERE id = ?"),
                eq("success"), eq(id1));
        verify(jdbcTemplate).update(
                eq("UPDATE target_schema.customer SET host_status = ?,target_timestamp = CURRENT_TIMESTAMP WHERE id = ?"),
                eq("failed"), eq(id2));
    }

    @Test
    void testAfterWrite_noNonSuccessRows_shouldNotCallRest() {
        // Arrange: DB returns empty list (all records already success)
        when(jdbcTemplate.queryForList(anyString())).thenReturn(Collections.emptyList());

        // Act
        Chunk<String> dummyChunk = new Chunk<>(List.of("item1"));
        listener.afterWrite(dummyChunk);

        // Assert: REST was never called
        verify(clientService, never()).getHostStatusByRefNo(anyString());
        verify(jdbcTemplate, never()).update(anyString(), any(), any());
    }

    @Test
    void testAfterWrite_restCallFails_shouldNotFailBatch() {
        // Arrange
        UUID id1 = UUID.fromString("11111111-1111-1111-1111-111111111111");
        Map<String, Object> row1 = new HashMap<>();
        row1.put("id", id1);
        row1.put("ref_no", "REF-001");
        row1.put("host_status", null);

        when(jdbcTemplate.queryForList(anyString())).thenReturn(List.of(row1));
        when(clientService.getHostStatusByRefNo("REF-001"))
                .thenThrow(new RuntimeException("Connection refused"));

        // Act — should NOT throw
        Chunk<String> dummyChunk = new Chunk<>(List.of("item1"));
        listener.afterWrite(dummyChunk);

        // Assert: REST was attempted, but no DB update occurred
        verify(clientService).getHostStatusByRefNo("REF-001");
        verify(jdbcTemplate, never()).update(anyString(), any(), any());
    }
}
