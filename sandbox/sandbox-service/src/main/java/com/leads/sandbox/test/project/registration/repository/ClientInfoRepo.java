package com.leads.sandbox.test.project.registration.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientInfoRepo extends JpaRepository<ClientInfoEntity, Long> {
    Optional<ClientInfoEntity> findByClientId(Long clientId);
}
