package com.leads.sandbox.test.project.registration.repository;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

@Entity
@Table(name = "client_info_turya")
public class ClientInfoEntity {

    @Column
    private String clientName;

    @Id
    @Column(name = "client_id", unique = true, nullable = false)
    private Long clientId;

    @Column
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column
    @CreationTimestamp
    private LocalDateTime createdAt;


    @OneToOne(
            mappedBy = "clientInfoEntity",
            cascade = CascadeType.ALL
    )
    private ClientDetailsEntity clientDetailsEntity;


    @OneToOne(
            mappedBy = "clientInfoEntity",
            cascade = CascadeType.ALL
    )
    private ClientAddressEntity clientAddressEntity;


    @OneToOne (
            mappedBy = "clientInfoEntity",
            cascade = CascadeType.ALL
    )
    private ClientAccountInfoEntity clientAccountInfoEntity;

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
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

    public ClientDetailsEntity getClientDetailsEntity() {
        return clientDetailsEntity;
    }

    public void setClientDetailsEntity(ClientDetailsEntity clientDetailsEntity) {
        this.clientDetailsEntity = clientDetailsEntity;
    }

    public ClientAddressEntity getClientAddressEntity() {
        return clientAddressEntity;
    }

    public void setClientAddressEntity(ClientAddressEntity clientAddressEntity) {
        this.clientAddressEntity = clientAddressEntity;
    }

    public ClientAccountInfoEntity getClientAccountInfoEntity() {
        return clientAccountInfoEntity;
    }

    public void setClientAccountInfoEntity(ClientAccountInfoEntity clientAccountInfoEntity) {
        this.clientAccountInfoEntity = clientAccountInfoEntity;
    }
}