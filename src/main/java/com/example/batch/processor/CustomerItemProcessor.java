package com.example.batch.processor;

import com.example.batch.model.CustomerSource;
import com.example.batch.model.CustomerTarget;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class CustomerItemProcessor implements ItemProcessor<CustomerSource, CustomerTarget> {

    @Override
    public CustomerTarget process(@NonNull CustomerSource source) {
        return CustomerTarget.builder()
                .id(UUID.randomUUID())
                .status(source.getStatus())
                .refNo(source.getRefNo())
                .createdAt(LocalDateTime.now())
                .build();
    }
}
