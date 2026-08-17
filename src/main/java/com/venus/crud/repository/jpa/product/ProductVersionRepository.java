package com.venus.crud.repository.jpa.product;

import com.venus.crud.entity.enums.VersionStatus;
import com.venus.crud.entity.product.ProductVersion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductVersionRepository extends JpaRepository<ProductVersion, Long> {

    List<ProductVersion> findByProductId(Long productId);
    Optional<ProductVersion> findByProductIdAndIsCurrentTrue(Long productId);
    Page<ProductVersion> findByStatus(VersionStatus status, Pageable pageable);
    List<ProductVersion> findByFormulaSignature(String formulaSignature);
}
