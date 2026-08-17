package com.venus.crud.repository.jpa.ingredient;

import com.venus.crud.entity.enums.RegulationStatus;
import com.venus.crud.entity.ingredient.Regulation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegulationRepository extends JpaRepository<Regulation, Long> {

    Page<Regulation> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    Page<Regulation> findByCountry(String country, Pageable pageable);
    Page<Regulation> findByAgency(String agency, Pageable pageable);
    Page<Regulation> findByStatus(RegulationStatus status, Pageable pageable);
}
