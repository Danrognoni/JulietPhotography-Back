package com.julietamarateo.photography.repository;

import com.julietamarateo.photography.entity.Photo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, String> {

    List<Photo> findAllByOrderByCreatedAtDesc();

    List<Photo> findByCategoryOrderByCreatedAtDesc(String category);

    @Query("SELECT p FROM Photo p WHERE " +
           "(:category IS NULL OR :category = '' OR :category = 'Todos' OR p.category = :category) AND " +
           "(:query IS NULL OR :query = '' OR " +
           "LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.technicalSheet) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.category) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "ORDER BY p.createdAt DESC")
    List<Photo> searchPhotos(@Param("category") String category, @Param("query") String query);

    @Query("SELECT p FROM Photo p WHERE " +
           "(:category IS NULL OR :category = '' OR :category = 'Todos' OR p.category = :category) AND " +
           "(:query IS NULL OR :query = '' OR " +
           "LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.technicalSheet) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.category) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Photo> searchPhotosPaged(@Param("category") String category, @Param("query") String query, Pageable pageable);
}
