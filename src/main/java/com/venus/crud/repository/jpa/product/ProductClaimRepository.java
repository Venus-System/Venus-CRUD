package com.venus.crud.repository.jpa.product;

import com.venus.crud.entity.enums.SourceType;
import com.venus.crud.entity.product.ProductClaim;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductClaimRepository extends JpaRepository<ProductClaim, Long> {

    @EntityGraph(attributePaths = "claim")
    List<ProductClaim> findByProductVersionId(Long productVersionId);
    Page<ProductClaim> findByClaimId(Long claimId, Pageable pageable);
    boolean existsByProductVersionIdAndClaimId(Long productVersionId, Long claimId);
    void deleteByProductVersionIdAndClaimId(Long productVersionId, Long claimId);
    List<ProductClaim> findByProductVersionIdAndWasVerifiedTrue(Long productVersionId);
    Page<ProductClaim> findBySourceType(SourceType sourceType, Pageable pageable);
}
