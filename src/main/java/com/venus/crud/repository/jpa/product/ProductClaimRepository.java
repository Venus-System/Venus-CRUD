package com.venus.crud.repository.jpa.product;

import com.venus.crud.entity.enums.SourceType;
import com.venus.crud.entity.product.ProductClaim;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductClaimRepository extends JpaRepository<ProductClaim, Long> {

    @EntityGraph(attributePaths = "claim")
    List<ProductClaim> findByProductVersionId(Long productVersionId);
    List<ProductClaim> findByProductVersionIdAndWasVerifiedTrue(Long productVersionId);
    Slice<ProductClaim> findByClaimId(Long claimId, Pageable pageable);
    Optional<ProductClaim> findByProductVersionIdAndClaimId(Long productVersionId, Long claimId);
    boolean existsByProductVersionIdAndClaimId(Long productVersionId, Long claimId);
    void deleteByProductVersionIdAndClaimId(Long productVersionId, Long claimId);
    Slice<ProductClaim> findBySourceType(SourceType sourceType, Pageable pageable);
    Slice<ProductClaim> findAllBy(Pageable pageable);
}
