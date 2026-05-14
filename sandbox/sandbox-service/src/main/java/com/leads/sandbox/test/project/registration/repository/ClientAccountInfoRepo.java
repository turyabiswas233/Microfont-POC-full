package com.leads.sandbox.test.project.registration.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientAccountInfoRepo extends CrudRepository<ClientAccountInfoEntity, Long> {
    Optional<ClientAccountInfoEntity> findByClientId(Long clientId);

    void deleteByClientId(Long id);
}
