package com.venus.crud.repository.jpa.product;

import com.venus.crud.entity.enums.VersionStatus;
import com.venus.crud.entity.product.ProductVersion;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductVersionRepository extends JpaRepository<ProductVersion, Long> {

    List<ProductVersion> findByProductId(Long productId);
    Slice<ProductVersion> findByProductId(Long productId, Pageable pageable);
    Optional<ProductVersion> findByProductIdAndIsCurrentTrue(Long productId);
    Slice<ProductVersion> findByStatus(VersionStatus status, Pageable pageable);
    Slice<ProductVersion> findByFormulaSignature(String formulaSignature, Pageable pageable);
    Slice<ProductVersion> findAllBy(Pageable pageable);
}
