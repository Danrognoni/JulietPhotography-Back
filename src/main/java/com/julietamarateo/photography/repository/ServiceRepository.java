package com.julietamarateo.photography.repository;

import com.julietamarateo.photography.entity.ServiceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<ServiceItem, String> {
    List<ServiceItem> findAllByOrderByCreatedAtAsc();
}
