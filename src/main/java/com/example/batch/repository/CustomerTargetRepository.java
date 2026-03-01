package com.example.batch.repository;

import com.example.batch.model.CustomerTarget;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CustomerTargetRepository
        extends JpaRepository<CustomerTarget, UUID> {

    @Modifying
    @Query("UPDATE CustomerTarget c SET c.hostStatus = :hostStatus WHERE c.refNo = :refNo")
    void updateHostStatusByRefNo(@Param("refNo") String refNo,
                                 @Param("hostStatus") String hostStatus);

    // Custom query to find customers whose status is not "success"
    List<CustomerTarget> findByStatusNot(String status);
}