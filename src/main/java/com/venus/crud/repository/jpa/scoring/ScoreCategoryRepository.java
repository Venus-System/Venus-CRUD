package com.venus.crud.repository.jpa.scoring;

import com.venus.crud.entity.scoring.ScoreCategory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ScoreCategoryRepository extends JpaRepository<ScoreCategory, Long> {

    Optional<ScoreCategory> findByNameIgnoreCase(String name);
    Slice<ScoreCategory> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Slice<ScoreCategory> findAllBy(Pageable pageable);
}