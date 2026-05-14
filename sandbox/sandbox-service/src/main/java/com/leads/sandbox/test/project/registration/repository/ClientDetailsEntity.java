package com.leads.sandbox.test.project.registration.repository;

import jakarta.annotation.Nullable;
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
    @Column(unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "client_id", unique = true, nullable = false)
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
    private Integer nidNumber;

    @Column
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column
    @CreationTimestamp
    private LocalDateTime createdAt;


    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "client_info_id",
            nullable = false,
            unique = true
    )
    private ClientInfoEntity clientInfoEntity;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Integer getNidNumber() {
        return nidNumber;
    }

    public void setNidNumber(Integer nidNumber) {
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