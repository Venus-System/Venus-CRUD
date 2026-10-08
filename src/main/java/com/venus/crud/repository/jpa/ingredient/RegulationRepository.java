package com.venus.crud.repository.jpa.ingredient;

import com.venus.crud.entity.enums.RegulationStatus;
import com.venus.crud.entity.ingredient.Regulation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RegulationRepository extends JpaRepository<Regulation, Long> {

    Optional<Regulation> findByDocumentUrl(String documentUrl);

    @Query("""
            select regulation from Regulation regulation
            where (cast(:title as String) is null or lower(regulation.title) like lower(concat('%', cast(:title as String), '%')))
              and (cast(:country as String) is null or regulation.country = :country)
              and (cast(:agency as String) is null or regulation.agency = :agency)
              and (cast(:status as String) is null or regulation.status = :status)
            """)
    Slice<Regulation> search(String title, String country, String agency, RegulationStatus status, Pageable pageable);
}