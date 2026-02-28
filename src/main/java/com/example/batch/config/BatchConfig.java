package com.example.batch.config;

import com.example.batch.listener.CustomerItemWriteListener;
import com.example.batch.model.CustomerSource;
import com.example.batch.model.CustomerTarget;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class BatchConfig {

        // 1. READER: Reads from Source and Target Schemas using CustomerSource
        @Bean
        public JdbcCursorItemReader<CustomerSource> reader(@Qualifier("sourceDataSource") DataSource sourceDataSource) {
                return new JdbcCursorItemReaderBuilder<CustomerSource>()
                                .name("customerReader")
                                .dataSource(sourceDataSource)
                                .sql("SELECT id, first_name, last_name, email FROM source_schema.customer " +
                                                "UNION " +
                                      "SELECT id, first_name, last_name, email FROM target_schema.customer")
                                .rowMapper(new BeanPropertyRowMapper<>(CustomerSource.class)) // Matches Lombok @Data
                                .build();
        }

        // 2. PROCESSOR: Transforms CustomerSource into CustomerTarget
        @Bean
        public ItemProcessor<CustomerSource, CustomerTarget> processor() {
                return source -> CustomerTarget.builder()
                                .id(java.util.UUID.randomUUID())
                                .firstName(source.getFirstName())
                                .lastName(source.getLastName())
                                .email(source.getEmail())
                                .build();
        }

        // 3. WRITER: Writes to Target Schema using CustomerTarget
        @Bean
        public JdbcBatchItemWriter<CustomerTarget> writer(@Qualifier("targetDataSource") DataSource targetDataSource) {
                return new JdbcBatchItemWriterBuilder<CustomerTarget>()
                                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                                .sql("INSERT INTO target_schema.customer (id, first_name, last_name, email) " +
                                                "VALUES (:id, :firstName, :lastName, :email) " +
                                                "ON CONFLICT (id) DO UPDATE SET " +
                                                "first_name = EXCLUDED.first_name, " +
                                                "last_name = EXCLUDED.last_name, " +
                                                "email = EXCLUDED.email")
                                .dataSource(targetDataSource)
                                .build();
        }

        // 4. STEP: Links Source -> Target
        @Bean
        public Step copyCustomerStep(JobRepository jobRepository,
                        PlatformTransactionManager transactionManager,
                        JdbcCursorItemReader<CustomerSource> reader,
                        ItemProcessor<CustomerSource, CustomerTarget> processor,
                        JdbcBatchItemWriter<CustomerTarget> writer,
                        CustomerItemWriteListener itemWriteListener) {
                return new StepBuilder("copyCustomerStep", jobRepository)
                                .<CustomerSource, CustomerTarget>chunk(10, transactionManager)
                                .reader(reader)
                                .processor(processor)
                                .writer(writer)
                                // .listener(itemWriteListener)
                                .build();
        }

        @Bean
        public Job copyCustomerJob(JobRepository jobRepository, Step copyCustomerStep) {
                return new JobBuilder("copyCustomerJob", jobRepository)
                                .incrementer(new RunIdIncrementer())
                                .start(copyCustomerStep)
                                .build();
        }
}