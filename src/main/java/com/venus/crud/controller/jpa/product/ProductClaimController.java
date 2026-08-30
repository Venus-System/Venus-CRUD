package com.venus.crud.controller.jpa.product;

import com.venus.crud.dto.jpa.patch.product.ProductClaimPatchRequest;
import com.venus.crud.dto.jpa.request.product.ProductClaimRequest;
import com.venus.crud.dto.jpa.response.product.ProductClaimResponse;
import com.venus.crud.entity.enums.SourceType;
import com.venus.crud.service.jpa.product.ProductClaimService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/product-claims")
public class ProductClaimController {

    private final ProductClaimService productClaimService;

    public ProductClaimController(ProductClaimService productClaimService) {
        this.productClaimService = productClaimService;
    }

    @GetMapping
    public ResponseEntity<List<ProductClaimResponse>> findAll() {
        return ResponseEntity.ok(productClaimService.findAll());
    }

    @GetMapping("/search")
    public ResponseEntity<Slice<ProductClaimResponse>> search(
            @RequestParam(required = false) Long claimId,
            @RequestParam(required = false) SourceType sourceType,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(productClaimService.search(claimId, sourceType, pageable));
    }

    @GetMapping("/product-version/{productVersionId}")
    public ResponseEntity<List<ProductClaimResponse>> findByProductVersionId(
            @PathVariable Long productVersionId, @RequestParam(required = false) Boolean wasVerified) {
        return ResponseEntity.ok(productClaimService.findByProductVersionId(productVersionId, wasVerified));
    }

    @PostMapping
    public ResponseEntity<ProductClaimResponse> create(@Valid @RequestBody ProductClaimRequest request) {
        ProductClaimResponse created = productClaimService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/product-version/{productVersionId}")
                .buildAndExpand(created.productVersionId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PatchMapping("/product-version/{productVersionId}/claim/{claimId}")
    public ResponseEntity<ProductClaimResponse> patch(
            @PathVariable Long productVersionId, @PathVariable Long claimId, @Valid @RequestBody ProductClaimPatchRequest request) {
        return ResponseEntity.ok(productClaimService.patch(productVersionId, claimId, request));
    }

    @DeleteMapping("/product-version/{productVersionId}/claim/{claimId}")
    public ResponseEntity<Void> delete(@PathVariable Long productVersionId, @PathVariable Long claimId) {
        productClaimService.delete(productVersionId, claimId);
        return ResponseEntity.noContent().build();
    }
}
