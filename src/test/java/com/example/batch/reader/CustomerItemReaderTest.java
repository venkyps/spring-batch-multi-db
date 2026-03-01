package com.example.batch.reader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.example.batch.model.CustomerSource;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.batch.item.database.JdbcCursorItemReader;

public class CustomerItemReaderTest {

    @Test
    void testReaderConfigurationWithSourceFlag() {
        // Arrange
        CustomerItemReader readerConfig = new CustomerItemReader();
        DataSource dataSource = Mockito.mock(DataSource.class);

        // Act
        JdbcCursorItemReader<CustomerSource> reader = readerConfig.reader(dataSource, "SOURCE");

        // Assert
        assertNotNull(reader);
        assertEquals(
                "SELECT id, ref_no, status FROM source_schema.customer where status in ('PENDING')",
                reader.getSql());
    }

    @Test
    void testReaderConfigurationWithTargetFlag() {
        // Arrange
        CustomerItemReader readerConfig = new CustomerItemReader();
        DataSource dataSource = Mockito.mock(DataSource.class);

        // Act
        JdbcCursorItemReader<CustomerSource> reader = readerConfig.reader(dataSource, "TARGET");

        // Assert
        assertNotNull(reader);
        assertEquals(
                "SELECT id, ref_no, status FROM target_schema.customer where status in ('PENDING')",
                reader.getSql());
    }

    @Test
    void testReaderConfigurationWithDefaultFlag() {
        // Arrange
        CustomerItemReader readerConfig = new CustomerItemReader();
        DataSource dataSource = Mockito.mock(DataSource.class);

        // Act
        JdbcCursorItemReader<CustomerSource> reader = readerConfig.reader(dataSource, null);

        // Assert
        assertNotNull(reader);
        assertEquals(
                "SELECT id, ref_no, status FROM source_schema.customer where status in ('PENDING') UNION SELECT id, ref_no, status FROM target_schema.customer where status in ('PENDING')",
                reader.getSql());
    }
}
