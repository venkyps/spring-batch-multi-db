package com.example.batch.model;

import java.util.UUID;

public class CustomerSource {

    private UUID id;
    private String refNo;
    private String status;

    public CustomerSource() {
    }

    public CustomerSource(UUID id, String refNo, String status) {
        this.id = id;
        this.refNo = refNo;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getRefNo() {
        return refNo;
    }

    public void setRefNo(String refNo) {
        this.refNo = refNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "CustomerSource{" +
                "id=" + id +
                ", refNo='" + refNo + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
