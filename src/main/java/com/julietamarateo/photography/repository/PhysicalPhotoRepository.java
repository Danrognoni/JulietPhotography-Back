package com.julietamarateo.photography.repository;

import com.julietamarateo.photography.entity.PhysicalPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhysicalPhotoRepository extends JpaRepository<PhysicalPhoto, String> {

    List<PhysicalPhoto> findByIsActiveTrueOrderByDisplayOrderAscCreatedAtDesc();

    List<PhysicalPhoto> findAllByOrderByDisplayOrderAscCreatedAtDesc();
}
