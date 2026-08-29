package com.venus.crud.controller.jpa.product;

import com.venus.crud.dto.jpa.patch.product.ProductVersionPatchRequest;
import com.venus.crud.dto.jpa.request.product.ProductVersionRequest;
import com.venus.crud.dto.jpa.response.product.ProductVersionResponse;
import com.venus.crud.entity.enums.VersionStatus;
import com.venus.crud.service.jpa.product.ProductVersionService;
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
@RequestMapping("/api/product-versions")
public class ProductVersionController {

    private final ProductVersionService productVersionService;

    public ProductVersionController(ProductVersionService productVersionService) {
        this.productVersionService = productVersionService;
    }

    @GetMapping
    public ResponseEntity<List<ProductVersionResponse>> findAll() {
        return ResponseEntity.ok(productVersionService.findAll());
    }

    @GetMapping("/search")
    public ResponseEntity<Slice<ProductVersionResponse>> search(
            @RequestParam(required = false) VersionStatus status,
            @RequestParam(required = false) String formulaSignature,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(productVersionService.search(status, formulaSignature, pageable));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ProductVersionResponse>> findByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(productVersionService.findByProductId(productId));
    }

    @GetMapping("/product/{productId}/current")
    public ResponseEntity<ProductVersionResponse> findCurrentByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(productVersionService.findCurrentByProductId(productId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductVersionResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(productVersionService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ProductVersionResponse> create(@Valid @RequestBody ProductVersionRequest request) {
        ProductVersionResponse created = productVersionService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductVersionResponse> update(@PathVariable Long id, @Valid @RequestBody ProductVersionRequest request) {
        return ResponseEntity.ok(productVersionService.update(id, request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProductVersionResponse> patch(@PathVariable Long id, @Valid @RequestBody ProductVersionPatchRequest request) {
        return ResponseEntity.ok(productVersionService.patch(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productVersionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
