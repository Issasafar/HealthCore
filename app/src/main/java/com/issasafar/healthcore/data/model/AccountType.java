package com.issasafar.healthcore.data.model;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public enum AccountType {
    PATIENT("patient"),
    MEDICAL_STAFF("medicalStaff");

    private final String value;

    AccountType(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
