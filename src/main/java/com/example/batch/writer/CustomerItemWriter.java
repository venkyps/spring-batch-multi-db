package com.example.batch.writer;

import com.example.batch.model.CustomerTarget;
import javax.sql.DataSource;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomerItemWriter {

    @Bean
    public JdbcBatchItemWriter<CustomerTarget> writer(@Qualifier("targetDataSource") DataSource targetDataSource) {
        return new JdbcBatchItemWriterBuilder<CustomerTarget>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO target_schema.customer (id, ref_no, status, created_at) "
                        + "VALUES (:id, :refNo, :status, CURRENT_TIMESTAMP) "
                        + "ON CONFLICT (id) DO UPDATE SET "
                        + "ref_no = EXCLUDED.ref_no, "
                        + "status = EXCLUDED.status, "
                        + "created_at = CURRENT_TIMESTAMP")
                .dataSource(targetDataSource)
                .build();
    }
}
