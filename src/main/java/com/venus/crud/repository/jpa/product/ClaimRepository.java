package com.venus.crud.repository.jpa.product;

import com.venus.crud.entity.enums.ClaimType;
import com.venus.crud.entity.product.Claim;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long> {

    Optional<Claim> findByNameIgnoreCase(String name);
    Slice<Claim> findByClaimType(ClaimType claimType, Pageable pageable);
    Slice<Claim> findAllBy(Pageable pageable);
}
