package com.venus.crud.repository.jpa.product;

import com.venus.crud.entity.product.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySlug(String slug);

    @Query("""
            select product from Product product
            where (cast(:name as String) is null or lower(product.name) like lower(concat('%', cast(:name as String), '%')))
              and (:brandId is null or product.brand.id = :brandId)
              and (:productCategoryId is null or product.productCategory.id = :productCategoryId)
              and (:isActive is null or product.isActive = :isActive)
            """)
    Slice<Product> search(String name, Long brandId, Long productCategoryId, Boolean isActive, Pageable pageable);
}
