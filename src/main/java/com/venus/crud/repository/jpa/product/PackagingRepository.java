package com.venus.crud.repository.jpa.product;

import com.venus.crud.entity.enums.PackagingMaterial;
import com.venus.crud.entity.product.Packaging;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PackagingRepository extends JpaRepository<Packaging, Long> {

    Optional<Packaging> findByProductVersionId(Long productVersionId);
    boolean existsByProductVersionId(Long productVersionId);
    void deleteByProductVersionId(Long productVersionId);
    Slice<Packaging> findByMaterial(PackagingMaterial material, Pageable pageable);
    Slice<Packaging> findByIsRecyclableTrue(Pageable pageable);
    Slice<Packaging> findByIsRefillableTrue(Pageable pageable);
    Slice<Packaging> findByIsBiodegradableTrue(Pageable pageable);
    Slice<Packaging> findAllBy(Pageable pageable);
}
