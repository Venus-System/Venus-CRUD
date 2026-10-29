package com.venus.crud.repository.jpa.product;

import com.venus.crud.entity.enums.SourceType;
import com.venus.crud.entity.product.ProductClaim;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductClaimRepository extends JpaRepository<ProductClaim, Long> {

    @EntityGraph(attributePaths = "claim")
    List<ProductClaim> findByProductVersionId(Long productVersionId);
    List<ProductClaim> findByProductVersionIdAndWasVerifiedTrue(Long productVersionId);
    Optional<ProductClaim> findByProductVersionIdAndClaimId(Long productVersionId, Long claimId);
    boolean existsByProductVersionIdAndClaimId(Long productVersionId, Long claimId);
    void deleteByProductVersionIdAndClaimId(Long productVersionId, Long claimId);

    @Query("""
            select productClaim from ProductClaim productClaim
            where (:claimId is null or productClaim.claim.id = :claimId)
              and (cast(:sourceType as String) is null or productClaim.sourceType = :sourceType)
            """)
    Slice<ProductClaim> search(Long claimId, SourceType sourceType, Pageable pageable);
}
