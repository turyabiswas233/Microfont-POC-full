package com.leads.sandbox.test.project.registration.repository;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "client_info_turya")
public class ClientInfoEntity {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String clientName;

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



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
        this.clientDetailsEntity = this.clientDetailsEntity;
    }

    public ClientAddressEntity getClientAddress() {
        return clientAddressEntity;
    }

    public void setClientAddress(ClientAddressEntity clientAddressEntity) {
        this.clientAddressEntity = clientAddressEntity;
    }

    public ClientAccountInfoEntity getClientAccountInfo() {
        return clientAccountInfoEntity;
    }

    public void setClientAccountInfo(ClientAccountInfoEntity clientAccountInfoEntity) {
        this.clientAccountInfoEntity = clientAccountInfoEntity;
    }
}