package com.venus.crud.repository.jpa.product;

import com.venus.crud.entity.enums.VersionStatus;
import com.venus.crud.entity.product.ProductVersion;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductVersionRepository extends JpaRepository<ProductVersion, Long> {

    List<ProductVersion> findByProductId(Long productId);
    Slice<ProductVersion> findByProductId(Long productId, Pageable pageable);
    Optional<ProductVersion> findByProductIdAndIsCurrentTrue(Long productId);
    Optional<ProductVersion> findByProductIdAndFormulaSignature(Long productId, String formulaSignature);

    @Query("""
            select productVersion from ProductVersion productVersion
            where (cast(:status as String) is null or productVersion.status = :status)
              and (cast(:formulaSignature as String) is null or productVersion.formulaSignature = :formulaSignature)
            """)
    Slice<ProductVersion> search(VersionStatus status, String formulaSignature, Pageable pageable);
}
