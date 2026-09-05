package com.julietamarateo.photography.repository;

import com.julietamarateo.photography.entity.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlbumRepository extends JpaRepository<Album, String> {

    List<Album> findAllByOrderByDisplayOrderAscCreatedAtAsc();

    Optional<Album> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);
}
