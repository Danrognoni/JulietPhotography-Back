package com.julietamarateo.photography.repository;

import com.julietamarateo.photography.entity.AlbumPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlbumPhotoRepository extends JpaRepository<AlbumPhoto, String> {

    List<AlbumPhoto> findByAlbumIdOrderByDisplayOrderAscCreatedAtAsc(String albumId);

    void deleteByAlbumId(String albumId);
}
