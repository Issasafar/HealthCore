package com.issasafar.healthcore.data.model;

public class MedicalStaff {
    private String fullName;
    private String email;
    private Gender gender;
    private AccountType accountType;

    public MedicalStaff() {

    }

    public MedicalStaff(String fullName, String email, Gender gender, AccountType accountType) {
        this.fullName = fullName;
        this.email = email;
        this.gender = gender;
        this.accountType = accountType;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }
}
