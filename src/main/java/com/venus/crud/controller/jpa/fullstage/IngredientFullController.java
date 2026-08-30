package com.venus.crud.controller.jpa.fullstage;

import com.venus.crud.dto.jpa.response.fullstage.IngredientFullResponse;
import com.venus.crud.service.jpa.fullstage.IngredientFullService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ingredients")
public class IngredientFullController {

    private final IngredientFullService ingredientFullService;

    public IngredientFullController(IngredientFullService ingredientFullService) {
        this.ingredientFullService = ingredientFullService;
    }

    @GetMapping("/{id}/full")
    public ResponseEntity<IngredientFullResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ingredientFullService.findById(id));
    }
}