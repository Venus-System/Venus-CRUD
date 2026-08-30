package com.venus.crud.repository.jpa.ingredient;

import com.venus.crud.entity.enums.RegulationStatus;
import com.venus.crud.entity.ingredient.Regulation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RegulationRepository extends JpaRepository<Regulation, Long> {

    Optional<Regulation> findByDocumentUrl(String documentUrl);
    Slice<Regulation> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    Slice<Regulation> findByCountry(String country, Pageable pageable);
    Slice<Regulation> findByAgency(String agency, Pageable pageable);
    Slice<Regulation> findByStatus(RegulationStatus status, Pageable pageable);
    Slice<Regulation> findAllBy(Pageable pageable);
}