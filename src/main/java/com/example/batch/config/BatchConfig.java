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
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BatchConfig {

    // 4. STEP: Links Source -> Target
    @Bean
    public Step copyCustomerStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            JdbcCursorItemReader<CustomerSource> reader,
            ItemProcessor<CustomerSource, CustomerTarget> processor,
            JdbcBatchItemWriter<CustomerTarget> writer,
            CustomerItemWriteListener itemWriteListener) {

        return new StepBuilder("copyCustomerStep", jobRepository)
                .<CustomerSource, CustomerTarget>chunk(50, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .listener(itemWriteListener)
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
