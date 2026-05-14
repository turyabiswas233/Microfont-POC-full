package com.leads.sandbox.test.project.register.query;

import java.util.Date;

public class RetrieveClientDetails {
    private String fatherName;
    private String motherName;
    private Date dateOfBirth;
    private String gender;
    private String maritalStatus;
    private String spouseName;
    private Integer nidNumber;

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public String getMotherName() {
        return motherName;
    }

    public void setMotherName(String motherName) {
        this.motherName = motherName;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getSpouseName() {
        return spouseName;
    }

    public void setSpouseName(String spouseName) {
        this.spouseName = spouseName;
    }

    public Integer getNidNumber() {
        return nidNumber;
    }

    public void setNidNumber(Integer nidNumber) {
        this.nidNumber = nidNumber;
    }
}
