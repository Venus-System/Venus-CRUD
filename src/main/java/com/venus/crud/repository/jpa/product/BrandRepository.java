package com.venus.crud.repository.jpa.product;

import com.venus.crud.entity.product.Brand;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {

    Optional<Brand> findByNameIgnoreCase(String name);
    Slice<Brand> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Slice<Brand> findByCountry(String country, Pageable pageable);
    Slice<Brand> findByHasCrueltyFreeClaimTrue(Pageable pageable);
    Slice<Brand> findByHasVeganClaimTrue(Pageable pageable);
    Slice<Brand> findByIsBrazilianTrue(Pageable pageable);
    Slice<Brand> findAllBy(Pageable pageable);
}
