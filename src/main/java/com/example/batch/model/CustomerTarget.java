package com.example.batch.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "customer", schema = "target_schema")
public class CustomerTarget {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(name = "ref_no")
    private String refNo;

    @Column(name = "status")
    private String status;

    @Column(name = "host_status")
    private String hostStatus;

    @Column(name = "target_timestamp")
    private java.time.LocalDateTime targetTimestamp;

    @Column(name = "created_at")
    private java.time.LocalDateTime createdAt;
}
