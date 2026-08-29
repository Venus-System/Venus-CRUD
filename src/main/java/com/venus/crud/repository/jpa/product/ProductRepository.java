package com.venus.crud.repository.jpa.product;

import com.venus.crud.entity.product.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySlug(String slug);
    Slice<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Slice<Product> findByBrandId(Long brandId, Pageable pageable);
    Slice<Product> findByProductCategoryId(Long productCategoryId, Pageable pageable);
    Slice<Product> findByBrandIdAndProductCategoryId(Long brandId, Long productCategoryId, Pageable pageable);
    Slice<Product> findByIsActiveTrue(Pageable pageable);
    Slice<Product> findAllBy(Pageable pageable);
}
