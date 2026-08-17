package com.venus.crud.repository.jpa.user;

import com.venus.crud.entity.enums.AllergyType;
import com.venus.crud.entity.user.Allergy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AllergyRepository extends JpaRepository<Allergy, Long> {

    Optional<Allergy> findByAllergyName(String allergyName);
    boolean existsByAllergyName(String allergyName);
    Page<Allergy> findByAllergyType(AllergyType allergyType, Pageable pageable);
}
