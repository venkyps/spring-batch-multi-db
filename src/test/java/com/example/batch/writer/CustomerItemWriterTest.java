package com.example.batch.writer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.example.batch.model.CustomerTarget;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.test.util.ReflectionTestUtils;

public class CustomerItemWriterTest {

    @Test
    void testWriterConfiguration() {
        // Arrange
        CustomerItemWriter writerConfig = new CustomerItemWriter();
        DataSource dataSource = Mockito.mock(DataSource.class);

        // Act
        JdbcBatchItemWriter<CustomerTarget> writer = writerConfig.writer(dataSource);

        // Assert
        assertNotNull(writer);
        String sql = (String) ReflectionTestUtils.getField(writer, "sql");
        assertEquals( "INSERT INTO target_schema.customer (id, ref_no, status, created_at) "
                + "VALUES (:id, :refNo, :status, CURRENT_TIMESTAMP) "
                + "ON CONFLICT (id) DO UPDATE SET " + "ref_no = EXCLUDED.ref_no, "
                + "status = EXCLUDED.status, "
                + "created_at = CURRENT_TIMESTAMP", sql);
    }
}
