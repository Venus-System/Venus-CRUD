package com.venus.crud.repository.jpa.product;

import com.venus.crud.entity.product.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {

    boolean existsByNameIgnoreCase(String name);
    Page<Brand> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<Brand> findByCountry(String country, Pageable pageable);
    Page<Brand> findByHasCrueltyFreeClaimTrue(Pageable pageable);
    Page<Brand> findByHasVeganClaimTrue(Pageable pageable);
    Page<Brand> findByIsBrazilianTrue(Pageable pageable);
}
