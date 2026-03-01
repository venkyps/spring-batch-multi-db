package com.example.batch.reader;

import com.example.batch.model.CustomerSource;
import javax.sql.DataSource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class CustomerItemReader {

    @Bean
    @StepScope
    public JdbcCursorItemReader<CustomerSource> reader(
            @Qualifier("sourceDataSource") DataSource sourceDataSource,
            @Value("#{jobParameters['schemaFlag']}") String schemaFlag) {

        return new JdbcCursorItemReaderBuilder<CustomerSource>()
                .name("customerReader")
                .dataSource(sourceDataSource)
                .sql(buildSql(schemaFlag))
                .rowMapper((rs, rowNum) -> {
                    CustomerSource customer = new CustomerSource();
                    // Explicitly mapping UUID from Native SQL Result Set
                    customer.setId((java.util.UUID) rs.getObject("id"));
                    customer.setRefNo(rs.getString("ref_no"));
                    customer.setStatus(rs.getString("status"));
                    return customer;
                })
                .build();
    }

    private String buildSql(String schemaFlag) {
        if ("SOURCE".equalsIgnoreCase(schemaFlag)) {
            return getSourceSchemaSql();
        } else if ("TARGET".equalsIgnoreCase(schemaFlag)) {
            return getTargetSchemaSql();
        } else {
            return getCombinedSchemaSql();
        }
    }

    private String getSourceSchemaSql() {
        return "SELECT id, ref_no, status FROM source_schema.customer where status in ('PENDING')";
    }

    private String getTargetSchemaSql() {
        return "SELECT id, ref_no, status FROM target_schema.customer where status in ('PENDING')";
    }

    private String getCombinedSchemaSql() {
        log.info("In the combined schema sql ::::::::::");
        return getSourceSchemaSql() + " UNION " + getTargetSchemaSql();
    }
}
