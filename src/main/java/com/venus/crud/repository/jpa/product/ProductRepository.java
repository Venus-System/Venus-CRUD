package com.venus.crud.repository.jpa.product;

import com.venus.crud.entity.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @EntityGraph(attributePaths = {"brand", "productCategory"})
    Optional<Product> findBySlug(String slug);
    boolean existsBySlug(String slug);
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<Product> findByBrandId(Long brandId, Pageable pageable);
    Page<Product> findByProductCategoryId(Long productCategoryId, Pageable pageable);
    Page<Product> findByIsActiveTrue(Pageable pageable);
}
