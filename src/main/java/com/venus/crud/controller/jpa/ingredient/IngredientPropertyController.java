package com.venus.crud.controller.jpa.ingredient;

import com.venus.crud.dto.jpa.patch.ingredient.IngredientPropertyPatchRequest;
import com.venus.crud.dto.jpa.request.ingredient.IngredientPropertyRequest;
import com.venus.crud.dto.jpa.response.ingredient.IngredientPropertyResponse;
import com.venus.crud.entity.enums.SourceType;
import com.venus.crud.service.jpa.ingredient.IngredientPropertyService;
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
@RequestMapping("/api/ingredient-properties")
public class IngredientPropertyController {

    private final IngredientPropertyService ingredientPropertyService;

    public IngredientPropertyController(IngredientPropertyService ingredientPropertyService) {
        this.ingredientPropertyService = ingredientPropertyService;
    }

    @GetMapping
    public ResponseEntity<List<IngredientPropertyResponse>> findAll() {
        return ResponseEntity.ok(ingredientPropertyService.findAll());
    }

    @GetMapping("/search")
    public ResponseEntity<Slice<IngredientPropertyResponse>> search(
            @RequestParam(required = false) SourceType sourceType,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ingredientPropertyService.search(sourceType, pageable));
    }

    @GetMapping("/ingredient/{ingredientId}")
    public ResponseEntity<List<IngredientPropertyResponse>> findByIngredientId(
            @PathVariable Long ingredientId, @RequestParam(required = false) String propertyName) {
        return ResponseEntity.ok(ingredientPropertyService.findByIngredientId(ingredientId, propertyName));
    }

    @GetMapping("/{id}")
    public ResponseEntity<IngredientPropertyResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ingredientPropertyService.findById(id));
    }

    @PostMapping
    public ResponseEntity<IngredientPropertyResponse> create(@Valid @RequestBody IngredientPropertyRequest request) {
        IngredientPropertyResponse created = ingredientPropertyService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<IngredientPropertyResponse> update(@PathVariable Long id, @Valid @RequestBody IngredientPropertyRequest request) {
        return ResponseEntity.ok(ingredientPropertyService.update(id, request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<IngredientPropertyResponse> patch(@PathVariable Long id, @Valid @RequestBody IngredientPropertyPatchRequest request) {
        return ResponseEntity.ok(ingredientPropertyService.patch(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ingredientPropertyService.delete(id);
        return ResponseEntity.noContent().build();
    }
}