package com.venus.crud.controller.jpa.product;

import com.venus.crud.dto.jpa.patch.product.BrandPatchRequest;
import com.venus.crud.dto.jpa.request.product.BrandRequest;
import com.venus.crud.dto.jpa.response.product.BrandResponse;
import com.venus.crud.service.jpa.product.BrandService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/brands")
public class BrandController {

    private final BrandService brandService;

    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @GetMapping
    public ResponseEntity<List<BrandResponse>> findAll() {
        return ResponseEntity.ok(brandService.findAll());
    }

    @GetMapping("/search")
    public ResponseEntity<Slice<BrandResponse>> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) Boolean hasCrueltyFreeClaim,
            @RequestParam(required = false) Boolean hasVeganClaim,
            @RequestParam(required = false) Boolean isBrazilian,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(brandService.search(name, country, hasCrueltyFreeClaim, hasVeganClaim, isBrazilian, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BrandResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(brandService.findById(id));
    }

    @PostMapping
    public ResponseEntity<BrandResponse> create(@Valid @RequestBody BrandRequest request) {
        BrandResponse created = brandService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BrandResponse> update(@PathVariable Long id, @Valid @RequestBody BrandRequest request) {
        return ResponseEntity.ok(brandService.update(id, request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BrandResponse> patch(@PathVariable Long id, @Valid @RequestBody BrandPatchRequest request) {
        return ResponseEntity.ok(brandService.patch(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        brandService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
