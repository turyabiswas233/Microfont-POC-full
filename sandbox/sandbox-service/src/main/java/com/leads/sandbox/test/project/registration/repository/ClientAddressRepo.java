package com.leads.sandbox.test.project.registration.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientAddressRepo extends JpaRepository<ClientAddressEntity, Long> {
    Optional<ClientAddressEntity> findByClientId(Long clientId);

    void deleteByClientId(Long id);
}
