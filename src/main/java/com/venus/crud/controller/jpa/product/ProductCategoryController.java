package com.venus.crud.controller.jpa.product;

import com.venus.crud.dto.jpa.patch.product.ProductCategoryPatchRequest;
import com.venus.crud.dto.jpa.request.product.ProductCategoryRequest;
import com.venus.crud.dto.jpa.response.product.ProductCategoryResponse;
import com.venus.crud.service.jpa.product.ProductCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/api/product-categories")
@Tag(name = "Categorias de Produto", description = "Árvore de categorias do catálogo.")
public class ProductCategoryController {

    private final ProductCategoryService productCategoryService;

    public ProductCategoryController(ProductCategoryService productCategoryService) {
        this.productCategoryService = productCategoryService;
    }

    @Operation(operationId = "productCategoryFindAll", summary = "Lista as categorias de produto")
    @GetMapping
    public ResponseEntity<List<ProductCategoryResponse>> findAll() {
        return ResponseEntity.ok(productCategoryService.findAll());
    }

    @Operation(operationId = "productCategorySearch", summary = "Busca as categorias de produto com filtros e paginação")
    @GetMapping("/search")
    public ResponseEntity<Slice<ProductCategoryResponse>> search(
            @RequestParam(required = false) String name,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(productCategoryService.search(name, pageable));
    }

    @Operation(operationId = "productCategoryFindById", summary = "Busca a categoria de produto por id")
    @GetMapping("/{id}")
    public ResponseEntity<ProductCategoryResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(productCategoryService.findById(id));
    }

    @Operation(operationId = "productCategoryCreate", summary = "Cadastra uma categoria de produto")
    @PostMapping
    public ResponseEntity<ProductCategoryResponse> create(@Valid @RequestBody ProductCategoryRequest request) {
        ProductCategoryResponse created = productCategoryService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @Operation(operationId = "productCategoryUpdate", summary = "Substitui os dados da categoria de produto")
    @PutMapping("/{id}")
    public ResponseEntity<ProductCategoryResponse> update(@PathVariable Long id, @Valid @RequestBody ProductCategoryRequest request) {
        return ResponseEntity.ok(productCategoryService.update(id, request));
    }

    @Operation(operationId = "productCategoryPatch", summary = "Atualiza parcialmente a categoria de produto")
    @PatchMapping("/{id}")
    public ResponseEntity<ProductCategoryResponse> patch(@PathVariable Long id, @Valid @RequestBody ProductCategoryPatchRequest request) {
        return ResponseEntity.ok(productCategoryService.patch(id, request));
    }

    @Operation(operationId = "productCategoryDelete", summary = "Remove a categoria de produto")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productCategoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
