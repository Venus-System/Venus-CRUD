package com.venus.crud.repository.jpa.product;

import com.venus.crud.entity.enums.ImageRole;
import com.venus.crud.entity.enums.SourceType;
import com.venus.crud.entity.product.ProductImage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    List<ProductImage> findByProductVersionId(Long productVersionId);
    Page<ProductImage> findByImageRole(ImageRole imageRole, Pageable pageable);
    Page<ProductImage> findBySourceType(SourceType sourceType, Pageable pageable);
    void deleteByProductVersionIdAndImageRole(Long productVersionId, ImageRole imageRole);
}
