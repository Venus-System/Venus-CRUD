package com.venus.crud.controller.jpa.product;

import com.venus.crud.dto.jpa.patch.product.ProductLabelPatchRequest;
import com.venus.crud.dto.jpa.request.product.ProductLabelRequest;
import com.venus.crud.dto.jpa.response.product.ProductLabelResponse;
import com.venus.crud.entity.enums.SourceType;
import com.venus.crud.service.jpa.product.ProductLabelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/api/product-labels")
@Tag(name = "Rótulos de Produto", description = "Rótulo capturado de um produto.")
public class ProductLabelController {

    private final ProductLabelService productLabelService;

    public ProductLabelController(ProductLabelService productLabelService) {
        this.productLabelService = productLabelService;
    }

    @Operation(operationId = "productLabelFindAll", summary = "Lista os rótulos")
    @GetMapping
    public ResponseEntity<List<ProductLabelResponse>> findAll() {
        return ResponseEntity.ok(productLabelService.findAll());
    }

    @Operation(operationId = "productLabelSearch", summary = "Busca os rótulos com filtros e paginação")
    @GetMapping("/search")
    public ResponseEntity<Slice<ProductLabelResponse>> search(
            @RequestParam(required = false) String language,
            @RequestParam(required = false) SourceType sourceType,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(productLabelService.search(language, sourceType, pageable));
    }

    @Operation(
            operationId = "productLabelFindBySourceReference",
            summary = "Busca o rótulo pela referência da fonte externa")
    @GetMapping("/source-reference/{sourceReference}")
    public ResponseEntity<ProductLabelResponse> findBySourceReference(
            @Parameter(description = "Identificador do rótulo na fonte externa de onde ele foi importado.")
            @PathVariable String sourceReference) {
        return ResponseEntity.ok(productLabelService.findBySourceReference(sourceReference));
    }

    @Operation(operationId = "productLabelFindByProductVersionId", summary = "Lista os rótulos de uma versão de produto")
    @GetMapping("/product-version/{productVersionId}")
    public ResponseEntity<ProductLabelResponse> findByProductVersionId(@PathVariable Long productVersionId) {
        return ResponseEntity.ok(productLabelService.findByProductVersionId(productVersionId));
    }

    @Operation(operationId = "productLabelCreate", summary = "Cadastra um rótulo")
    @PostMapping
    public ResponseEntity<ProductLabelResponse> create(@Valid @RequestBody ProductLabelRequest request) {
        ProductLabelResponse created = productLabelService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/product-version/{productVersionId}")
                .buildAndExpand(created.productVersionId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @Operation(operationId = "productLabelUpdate", summary = "Substitui os dados do rótulo")
    @PutMapping("/product-version/{productVersionId}")
    public ResponseEntity<ProductLabelResponse> update(@PathVariable Long productVersionId, @Valid @RequestBody ProductLabelRequest request) {
        return ResponseEntity.ok(productLabelService.update(productVersionId, request));
    }

    @Operation(operationId = "productLabelPatch", summary = "Atualiza parcialmente o rótulo")
    @PatchMapping("/product-version/{productVersionId}")
    public ResponseEntity<ProductLabelResponse> patch(@PathVariable Long productVersionId, @Valid @RequestBody ProductLabelPatchRequest request) {
        return ResponseEntity.ok(productLabelService.patch(productVersionId, request));
    }

    @Operation(operationId = "productLabelDelete", summary = "Remove o rótulo")
    @DeleteMapping("/product-version/{productVersionId}")
    public ResponseEntity<Void> delete(@PathVariable Long productVersionId) {
        productLabelService.delete(productVersionId);
        return ResponseEntity.noContent().build();
    }
}
