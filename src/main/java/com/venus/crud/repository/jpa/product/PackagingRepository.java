package com.venus.crud.repository.jpa.product;

import com.venus.crud.entity.enums.PackagingMaterial;
import com.venus.crud.entity.product.Packaging;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PackagingRepository extends JpaRepository<Packaging, Long> {

    Optional<Packaging> findByProductVersionId(Long productVersionId);
    boolean existsByProductVersionId(Long productVersionId);
    void deleteByProductVersionId(Long productVersionId);

    @Query("""
            select packaging from Packaging packaging
            where (cast(:material as String) is null or packaging.material = :material)
              and (:isRecyclable is null or packaging.isRecyclable = :isRecyclable)
              and (:isRefillable is null or packaging.isRefillable = :isRefillable)
              and (:isBiodegradable is null or packaging.isBiodegradable = :isBiodegradable)
            """)
    Slice<Packaging> search(PackagingMaterial material, Boolean isRecyclable, Boolean isRefillable,
            Boolean isBiodegradable, Pageable pageable);
}
