package com.issasafar.healthcore.data.model;

public enum HealthStatus {
    HEALTHY("healthy"),
    UNHEALTHY("unhealthy");

    private String value;

    HealthStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
