package com.venus.crud.repository.jpa.product;

import com.venus.crud.entity.product.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {

    Optional<ProductCategory> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
}
