package com.leads.sandbox.test.project.registration.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientDetailsRepo extends JpaRepository<ClientDetailsEntity, Long> {
    Optional<ClientDetailsEntity> findByClientId(Long clientId);

    void deleteByClientId(Long id);
}
