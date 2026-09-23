package com.venus.crud.controller.jpa.fullstage;

import com.venus.crud.dto.jpa.response.fullstage.ProductFullResponse;
import com.venus.crud.service.jpa.fullstage.ProductFullService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Produtos", description = "Catálogo de produtos e o agregado com marca, categoria, versões e fotos.")
public class ProductFullController {

    private final ProductFullService productFullService;

    public ProductFullController(ProductFullService productFullService) {
        this.productFullService = productFullService;
    }

    @Operation(operationId = "productFullFindById", summary = "Busca o produto completo por id")
    @GetMapping("/{id}/full")
    public ResponseEntity<ProductFullResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(productFullService.findById(id));
    }
}
