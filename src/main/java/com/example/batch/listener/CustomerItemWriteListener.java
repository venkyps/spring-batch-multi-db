package com.example.batch.listener;

import com.example.batch.model.CustomerTarget;
import com.example.batch.service.CustomerTargetClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.item.Chunk;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CustomerItemWriteListener implements ItemWriteListener<CustomerTarget> {

    private static final Logger log = LoggerFactory.getLogger(CustomerItemWriteListener.class);
    private final CustomerTargetClientService clientService;

    public CustomerItemWriteListener(CustomerTargetClientService clientService) {
        this.clientService = clientService;
    }

    @Override
    public void afterWrite(Chunk<? extends CustomerTarget> items) {
        log.info("Finished writing a chunk of {} items to the DB.", items.size());

        for (CustomerTarget customer : items) {
            String email = customer.getEmail();
            log.info("Retrieving info for email: {}", email);
            try {
                // Call the RestTemplate service to fetch the data
                List<CustomerTarget> fetchedData = clientService.getCustomersByEmail(email);
                log.info("Successfully fetched {} results from REST endpoint for email {}", fetchedData.size(), email);
            } catch (Exception e) {
                // We're catching the exception so a failed REST call doesn't fail the entire
                // batch job
                log.error("Failed to retrieve data for email {} from REST endpoint: {}", email, e.getMessage());
            }
        }
    }
}
