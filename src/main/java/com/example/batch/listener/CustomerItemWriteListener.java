package com.example.batch.listener;

import com.example.batch.model.HostStatusResponse;
import com.example.batch.service.CustomerTargetClientService;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.item.Chunk;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class CustomerItemWriteListener implements ItemWriteListener<Object> {

    private static final Logger log = LoggerFactory.getLogger(CustomerItemWriteListener.class);
    private final CustomerTargetClientService clientService;
    private final JdbcTemplate jdbcTemplate;

    public CustomerItemWriteListener(
            CustomerTargetClientService clientService,
            @Qualifier("targetDataSource") DataSource targetDataSource) {
        this.clientService = clientService;
        this.jdbcTemplate = new JdbcTemplate(targetDataSource);
    }

    @Override
    public void afterWrite(Chunk<?> items) {
        log.info("Chunk of {} items written. Now retrieving non-success records from target_schema.customer...",
                items.size());

        // Query ALL rows from target_schema.customer where host_status is NOT 'success'
        String selectSql = "SELECT id, ref_no, host_status FROM target_schema.customer "
                + "WHERE host_status IS NULL OR LOWER(host_status) <> 'success'";

        List<Map<String, Object>> pendingStatus = jdbcTemplate.queryForList(selectSql);
        log.info("Found {} record(s) with non-success host_status.", pendingStatus.size());

        for (Map<String, Object> row : pendingStatus) {
            UUID id = (UUID) row.get("id");
            String refNo = (String) row.get("ref_no");

            log.info("Retrieving host_status from REST endpoint for refNo={}", refNo);
            try {
                // Call the REST endpoint to fetch the host_status
                HostStatusResponse hostStatus = clientService.getHostStatusByRefNo(refNo);
                log.info("Received host_status='{}' for refNo={}", hostStatus.getHostStatus(), refNo);

                // Persist the retrieved host_status into the target_schema
                int rowsUpdated = jdbcTemplate.update(
                        "UPDATE target_schema.customer SET host_status = ?,target_timestamp = CURRENT_TIMESTAMP WHERE id = ?",
                        hostStatus.getHostStatus(), id);
                log.info("Updated {} row(s) for id={}", rowsUpdated, id);

            } catch (Exception e) {
                log.error("Failed to retrieve/update host_status for refNo={}: {}", refNo, e.getMessage());
            }
        }
    }
}
