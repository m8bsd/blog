package com.example.blog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByOrderByCreatedAtDesc();

    List<Post> findByCategoryIdOrderByCreatedAtDesc(Long categoryId);

    @Modifying
    @Query("update Post p set p.category = null where p.category.id = :categoryId")
    void clearCategory(@Param("categoryId") Long categoryId);
}
