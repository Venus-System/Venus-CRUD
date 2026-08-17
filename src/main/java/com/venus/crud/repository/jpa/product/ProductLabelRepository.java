package com.venus.crud.repository.jpa.product;

import com.venus.crud.entity.enums.SourceType;
import com.venus.crud.entity.product.ProductLabel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductLabelRepository extends JpaRepository<ProductLabel, Long> {

    Optional<ProductLabel> findByProductVersionId(Long productVersionId);
    Page<ProductLabel> findByLanguage(String language, Pageable pageable);
    Page<ProductLabel> findBySourceType(SourceType sourceType, Pageable pageable);
    Optional<ProductLabel> findBySourceReference(String sourceReference);
}
