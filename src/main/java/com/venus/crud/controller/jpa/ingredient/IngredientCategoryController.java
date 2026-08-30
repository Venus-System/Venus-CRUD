package com.venus.crud.controller.jpa.ingredient;

import com.venus.crud.dto.jpa.patch.ingredient.IngredientCategoryPatchRequest;
import com.venus.crud.dto.jpa.request.ingredient.IngredientCategoryRequest;
import com.venus.crud.dto.jpa.response.ingredient.IngredientCategoryResponse;
import com.venus.crud.service.jpa.ingredient.IngredientCategoryService;
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
@RequestMapping("/api/ingredient-categories")
public class IngredientCategoryController {

    private final IngredientCategoryService ingredientCategoryService;

    public IngredientCategoryController(IngredientCategoryService ingredientCategoryService) {
        this.ingredientCategoryService = ingredientCategoryService;
    }

    @GetMapping
    public ResponseEntity<List<IngredientCategoryResponse>> findAll() {
        return ResponseEntity.ok(ingredientCategoryService.findAll());
    }

    @GetMapping("/search")
    public ResponseEntity<Slice<IngredientCategoryResponse>> search(
            @RequestParam(required = false) String name,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ingredientCategoryService.search(name, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<IngredientCategoryResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ingredientCategoryService.findById(id));
    }

    @PostMapping
    public ResponseEntity<IngredientCategoryResponse> create(@Valid @RequestBody IngredientCategoryRequest request) {
        IngredientCategoryResponse created = ingredientCategoryService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<IngredientCategoryResponse> update(@PathVariable Long id, @Valid @RequestBody IngredientCategoryRequest request) {
        return ResponseEntity.ok(ingredientCategoryService.update(id, request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<IngredientCategoryResponse> patch(@PathVariable Long id, @Valid @RequestBody IngredientCategoryPatchRequest request) {
        return ResponseEntity.ok(ingredientCategoryService.patch(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ingredientCategoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}