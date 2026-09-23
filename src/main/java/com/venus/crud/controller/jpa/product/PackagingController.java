package com.venus.crud.controller.jpa.product;

import com.venus.crud.dto.jpa.patch.product.PackagingPatchRequest;
import com.venus.crud.dto.jpa.request.product.PackagingRequest;
import com.venus.crud.dto.jpa.response.product.PackagingResponse;
import com.venus.crud.entity.enums.PackagingMaterial;
import com.venus.crud.service.jpa.product.PackagingService;
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
@RequestMapping("/api/packagings")
@Tag(name = "Embalagens", description = "Tipos de embalagem e material.")
public class PackagingController {

    private final PackagingService packagingService;

    public PackagingController(PackagingService packagingService) {
        this.packagingService = packagingService;
    }

    @Operation(operationId = "packagingFindAll", summary = "Lista as embalagens")
    @GetMapping
    public ResponseEntity<List<PackagingResponse>> findAll() {
        return ResponseEntity.ok(packagingService.findAll());
    }

    @Operation(operationId = "packagingSearch", summary = "Busca as embalagens com filtros e paginação")
    @GetMapping("/search")
    public ResponseEntity<Slice<PackagingResponse>> search(
            @RequestParam(required = false) PackagingMaterial material,
            @RequestParam(required = false) Boolean isRecyclable,
            @RequestParam(required = false) Boolean isRefillable,
            @RequestParam(required = false) Boolean isBiodegradable,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(packagingService.search(material, isRecyclable, isRefillable, isBiodegradable, pageable));
    }

    @Operation(operationId = "packagingFindByProductVersionId", summary = "Busca a embalagem de uma versão de produto")
    @GetMapping("/product-version/{productVersionId}")
    public ResponseEntity<PackagingResponse> findByProductVersionId(@PathVariable Long productVersionId) {
        return ResponseEntity.ok(packagingService.findByProductVersionId(productVersionId));
    }

    @Operation(operationId = "packagingCreate", summary = "Cadastra uma embalagem")
    @PostMapping
    public ResponseEntity<PackagingResponse> create(@Valid @RequestBody PackagingRequest request) {
        PackagingResponse created = packagingService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/product-version/{productVersionId}")
                .buildAndExpand(created.productVersionId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @Operation(operationId = "packagingUpdate", summary = "Substitui os dados da embalagem")
    @PutMapping("/product-version/{productVersionId}")
    public ResponseEntity<PackagingResponse> update(@PathVariable Long productVersionId, @Valid @RequestBody PackagingRequest request) {
        return ResponseEntity.ok(packagingService.update(productVersionId, request));
    }

    @Operation(operationId = "packagingPatch", summary = "Atualiza parcialmente a embalagem")
    @PatchMapping("/product-version/{productVersionId}")
    public ResponseEntity<PackagingResponse> patch(@PathVariable Long productVersionId, @Valid @RequestBody PackagingPatchRequest request) {
        return ResponseEntity.ok(packagingService.patch(productVersionId, request));
    }

    @Operation(operationId = "packagingDelete", summary = "Remove a embalagem")
    @DeleteMapping("/product-version/{productVersionId}")
    public ResponseEntity<Void> delete(@PathVariable Long productVersionId) {
        packagingService.delete(productVersionId);
        return ResponseEntity.noContent().build();
    }
}
