package com.venus.crud.repository.jpa.product;

import com.venus.crud.entity.enums.SourceType;
import com.venus.crud.entity.product.ProductLabel;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductLabelRepository extends JpaRepository<ProductLabel, Long> {

    Optional<ProductLabel> findByProductVersionId(Long productVersionId);
    boolean existsByProductVersionId(Long productVersionId);
    void deleteByProductVersionId(Long productVersionId);
    Optional<ProductLabel> findBySourceReference(String sourceReference);
    Slice<ProductLabel> findByLanguage(String language, Pageable pageable);
    Slice<ProductLabel> findBySourceType(SourceType sourceType, Pageable pageable);
    Slice<ProductLabel> findAllBy(Pageable pageable);
}
