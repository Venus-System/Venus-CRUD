package com.venus.crud.repository.jpa.product;

import com.venus.crud.entity.enums.PackagingMaterial;
import com.venus.crud.entity.product.Packaging;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PackagingRepository extends JpaRepository<Packaging, Long> {

    Optional<Packaging> findByProductVersionId(Long productVersionId);
    Page<Packaging> findByMaterial(PackagingMaterial material, Pageable pageable);
    Page<Packaging> findByIsRecyclableTrue(Pageable pageable);
    Page<Packaging> findByIsRefillableTrue(Pageable pageable);
    Page<Packaging> findByIsBiodegradableTrue(Pageable pageable);
}
