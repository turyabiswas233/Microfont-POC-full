package com.leads.sandbox.test.project.address.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThanaRepository extends JpaRepository<ThanaEntity, Long> {
    List<ThanaEntity> findThanaEntitiesByDistrictEntityId(Long districtId);
}
