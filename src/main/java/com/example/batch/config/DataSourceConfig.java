package com.example.batch.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class DataSourceConfig {

    // Primary DataSource for Spring Batch Metadata tables
    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties batchDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    public HikariDataSource batchDataSource(DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    // Source Schema DataSource
    @Bean
    @ConfigurationProperties("app.datasource.source")
    public DataSourceProperties sourceDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public HikariDataSource sourceDataSource(DataSourceProperties sourceDataSourceProperties) {
        return sourceDataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    // Target Schema DataSource
    @Bean
    @ConfigurationProperties("app.datasource.target")
    public DataSourceProperties targetDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public HikariDataSource targetDataSource(DataSourceProperties targetDataSourceProperties) {
        return targetDataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }
}