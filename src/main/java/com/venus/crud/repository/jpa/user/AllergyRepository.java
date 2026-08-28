package com.venus.crud.repository.jpa.user;

import com.venus.crud.entity.enums.AllergyType;
import com.venus.crud.entity.user.Allergy;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AllergyRepository extends JpaRepository<Allergy, Long> {

    Optional<Allergy> findByAllergyName(String allergyName);
    boolean existsByAllergyName(String allergyName);
    Slice<Allergy> findByAllergyType(AllergyType allergyType, Pageable pageable);
    Slice<Allergy> findAllBy(Pageable pageable);
}
