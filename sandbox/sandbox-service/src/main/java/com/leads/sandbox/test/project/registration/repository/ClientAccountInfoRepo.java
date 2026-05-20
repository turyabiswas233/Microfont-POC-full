package com.leads.sandbox.test.project.registration.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientAccountInfoRepo extends CrudRepository<ClientAccountInfoEntity, Long> {
    Optional<ClientAccountInfoEntity> findByClientId(Long clientId);
}
