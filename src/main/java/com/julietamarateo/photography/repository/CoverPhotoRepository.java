package com.julietamarateo.photography.repository;

import com.julietamarateo.photography.entity.CoverPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CoverPhotoRepository extends JpaRepository<CoverPhoto, Long> {

    Optional<CoverPhoto> findTopByOrderByIdAsc();

    Optional<CoverPhoto> findTopByOrderByUpdatedAtDesc();
}
