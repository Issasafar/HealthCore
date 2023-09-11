package com.issasafar.healthcore.data.model;

public enum Gender {
    MALE("male"),
    FEMALE("female"),
    OTHER("other");
    private final String value;

    Gender(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}


