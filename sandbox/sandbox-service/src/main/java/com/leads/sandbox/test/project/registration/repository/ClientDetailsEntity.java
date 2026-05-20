package com.leads.sandbox.test.project.registration.repository;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "client_details_turya")
public class ClientDetailsEntity {


    @Id
    @NotNull
    @Column(name = "client_id", unique = true)
    private Long clientId;

    @NotNull
    private String fatherName;
    @NotNull
    private String motherName;
    @NotNull
    private Date dateOfBirth;
    @NotNull
    private String gender;
    @NotNull
    private String maritalStatus;
    private String spouseName;
    @NotNull
    private Long nidNumber;

    @Column
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column
    @CreationTimestamp
    private LocalDateTime createdAt;


    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "client_id",
            referencedColumnName = "client_id",
            updatable = false,
            insertable = false
    )
    private ClientInfoEntity clientInfoEntity;

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

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

    public Long getNidNumber() {
        return nidNumber;
    }

    public void setNidNumber(Long nidNumber) {
        this.nidNumber = nidNumber;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ClientInfoEntity getClientInfoEntity() {
        return clientInfoEntity;
    }

    public void setClientInfoEntity(ClientInfoEntity clientInfoEntity) {
        this.clientInfoEntity = clientInfoEntity;
    }
}