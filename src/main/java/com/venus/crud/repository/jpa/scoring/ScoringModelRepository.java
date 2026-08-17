package com.venus.crud.repository.jpa.scoring;

import com.venus.crud.entity.scoring.ScoringModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ScoringModelRepository extends JpaRepository<ScoringModel, Long> {

    Optional<ScoringModel> findByIsActiveTrue();
    Optional<ScoringModel> findByNameAndVersion(String name, String version);
    boolean existsByNameAndVersion(String name, String version);
}
