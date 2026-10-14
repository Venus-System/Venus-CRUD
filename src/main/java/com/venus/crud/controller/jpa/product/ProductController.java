package com.venus.crud.controller.jpa.product;

import com.venus.crud.dto.jpa.patch.product.ProductPatchRequest;
import com.venus.crud.dto.jpa.request.product.ProductRequest;
import com.venus.crud.dto.jpa.response.product.ProductResponse;
import com.venus.crud.service.jpa.product.ProductService;
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
@RequestMapping("/api/products")
@Tag(name = "Produtos", description = "Catálogo de produtos e o agregado com marca, categoria, versões e fotos.")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(operationId = "productFindAll", summary = "Lista os produtos")
    @GetMapping
    public ResponseEntity<List<ProductResponse>> findAll() {
        return ResponseEntity.ok(productService.findAll());
    }

    @Operation(
            operationId = "productSearch",
            summary = "Busca os produtos com filtros e paginação",
            description = "Os filtros se combinam: o produto precisa atender a todos os informados. Sem nenhum, "
                    + "lista todos.\n\n`name` e `ingredient` não diferenciam maiúsculas nem acento: `locao` acha "
                    + "\"Loção\".\n\n`ingredient` olha só a **versão atual** do produto. `hasCrueltyFreeClaim`, "
                    + "`hasVeganClaim` e `isBrazilian` são os selos da **marca**; `false` traz as marcas sem o selo.")
    @GetMapping("/search")
    public ResponseEntity<Slice<ProductResponse>> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long brandId,
            @RequestParam(required = false) Long productCategoryId,
            @RequestParam(required = false) Boolean isActive,
            @Parameter(description = "Trecho do nome INCI ou do nome comum de algum ingrediente da versão atual, "
                    + "sem diferenciar maiúsculas nem acento. Ex.: lavand.")
            @RequestParam(required = false) String ingredient,
            @RequestParam(required = false) Boolean hasCrueltyFreeClaim,
            @RequestParam(required = false) Boolean hasVeganClaim,
            @RequestParam(required = false) Boolean isBrazilian,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(productService.search(name, brandId, productCategoryId, isActive, ingredient,
                hasCrueltyFreeClaim, hasVeganClaim, isBrazilian, pageable));
    }

    @Operation(operationId = "productFindById", summary = "Busca o produto por id")
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @Operation(operationId = "productCreate", summary = "Cadastra um produto")
    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request) {
        ProductResponse created = productService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @Operation(operationId = "productUpdate", summary = "Substitui os dados do produto")
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.update(id, request));
    }

    @Operation(operationId = "productPatch", summary = "Atualiza parcialmente o produto")
    @PatchMapping("/{id}")
    public ResponseEntity<ProductResponse> patch(@PathVariable Long id, @Valid @RequestBody ProductPatchRequest request) {
        return ResponseEntity.ok(productService.patch(id, request));
    }

    @Operation(operationId = "productDelete", summary = "Remove o produto")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
