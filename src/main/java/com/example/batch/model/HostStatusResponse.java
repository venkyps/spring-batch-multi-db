package com.example.batch.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HostStatusResponse {

    private String hostStatus;

    public HostStatusResponse() {
    }

    public String getHostStatus() {
        return hostStatus;
    }

    public void setHostStatus(String hostStatus) {
        this.hostStatus = hostStatus;
    }

    @Override
    public String toString() {
        return "HostStatusResponse{hostStatus='" + hostStatus + "'}";
    }
}
