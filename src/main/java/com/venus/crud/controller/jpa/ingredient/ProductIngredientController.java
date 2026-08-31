package com.venus.crud.controller.jpa.ingredient;

import com.venus.crud.dto.jpa.patch.ingredient.ProductIngredientPatchRequest;
import com.venus.crud.dto.jpa.request.ingredient.ProductIngredientRequest;
import com.venus.crud.dto.jpa.response.ingredient.ProductIngredientResponse;
import com.venus.crud.service.jpa.ingredient.ProductIngredientService;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/product-ingredients")
public class ProductIngredientController {

    private final ProductIngredientService productIngredientService;

    public ProductIngredientController(ProductIngredientService productIngredientService) {
        this.productIngredientService = productIngredientService;
    }

    @GetMapping
    public ResponseEntity<List<ProductIngredientResponse>> findAll() {
        return ResponseEntity.ok(productIngredientService.findAll());
    }

    @GetMapping("/product-version/{productVersionId}")
    public ResponseEntity<List<ProductIngredientResponse>> findByProductVersionId(@PathVariable Long productVersionId) {
        return ResponseEntity.ok(productIngredientService.findByProductVersionId(productVersionId));
    }

    @GetMapping("/ingredient/{ingredientId}")
    public ResponseEntity<Slice<ProductIngredientResponse>> findByIngredientId(
            @PathVariable Long ingredientId, @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(productIngredientService.findByIngredientId(ingredientId, pageable));
    }

    @PostMapping
    public ResponseEntity<ProductIngredientResponse> create(@Valid @RequestBody ProductIngredientRequest request) {
        ProductIngredientResponse created = productIngredientService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/product-version/{productVersionId}")
                .buildAndExpand(created.productVersionId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PatchMapping("/product-version/{productVersionId}/ingredient/{ingredientId}")
    public ResponseEntity<ProductIngredientResponse> patch(
            @PathVariable Long productVersionId, @PathVariable Long ingredientId,
            @Valid @RequestBody ProductIngredientPatchRequest request) {
        return ResponseEntity.ok(productIngredientService.patch(productVersionId, ingredientId, request));
    }

    @DeleteMapping("/product-version/{productVersionId}/ingredient/{ingredientId}")
    public ResponseEntity<Void> delete(@PathVariable Long productVersionId, @PathVariable Long ingredientId) {
        productIngredientService.delete(productVersionId, ingredientId);
        return ResponseEntity.noContent().build();
    }
}