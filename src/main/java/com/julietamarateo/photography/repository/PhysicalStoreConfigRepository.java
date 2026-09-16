package com.julietamarateo.photography.repository;

import com.julietamarateo.photography.entity.PhysicalStoreConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PhysicalStoreConfigRepository extends JpaRepository<PhysicalStoreConfig, Long> {

    Optional<PhysicalStoreConfig> findTopByOrderByIdAsc();
}
