package com.venus.crud.controller.jpa.fullstage;

import com.venus.crud.dto.jpa.response.fullstage.ProductFullResponse;
import com.venus.crud.service.jpa.fullstage.ProductFullService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductFullController {

    private final ProductFullService productFullService;

    public ProductFullController(ProductFullService productFullService) {
        this.productFullService = productFullService;
    }

    @GetMapping("/{id}/full")
    public ResponseEntity<ProductFullResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(productFullService.findById(id));
    }
}
