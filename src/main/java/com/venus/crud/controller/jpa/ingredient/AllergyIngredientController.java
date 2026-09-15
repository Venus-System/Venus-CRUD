package com.venus.crud.controller.jpa.ingredient;

import com.venus.crud.dto.jpa.patch.ingredient.AllergyIngredientPatchRequest;
import com.venus.crud.dto.jpa.request.ingredient.AllergyIngredientRequest;
import com.venus.crud.dto.jpa.response.ingredient.AllergyIngredientResponse;
import com.venus.crud.entity.enums.SourceType;
import com.venus.crud.service.jpa.ingredient.AllergyIngredientService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/allergy-ingredients")
public class AllergyIngredientController {

    private final AllergyIngredientService allergyIngredientService;

    public AllergyIngredientController(AllergyIngredientService allergyIngredientService) {
        this.allergyIngredientService = allergyIngredientService;
    }

    @GetMapping
    public ResponseEntity<List<AllergyIngredientResponse>> findAll() {
        return ResponseEntity.ok(allergyIngredientService.findAll());
    }

    @GetMapping("/search")
    public ResponseEntity<Slice<AllergyIngredientResponse>> search(
            @RequestParam(required = false) Long ingredientId,
            @RequestParam(required = false) SourceType sourceType,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(allergyIngredientService.search(ingredientId, sourceType, pageable));
    }

    @GetMapping("/allergy/{allergyId}")
    public ResponseEntity<List<AllergyIngredientResponse>> findByAllergyId(@PathVariable Long allergyId) {
        return ResponseEntity.ok(allergyIngredientService.findByAllergyId(allergyId));
    }

    @PostMapping
    public ResponseEntity<AllergyIngredientResponse> create(@Valid @RequestBody AllergyIngredientRequest request) {
        AllergyIngredientResponse created = allergyIngredientService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/allergy/{allergyId}")
                .buildAndExpand(created.allergyId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PatchMapping("/allergy/{allergyId}/ingredient/{ingredientId}")
    public ResponseEntity<AllergyIngredientResponse> patch(
            @PathVariable Long allergyId, @PathVariable Long ingredientId, @Valid @RequestBody AllergyIngredientPatchRequest request) {
        return ResponseEntity.ok(allergyIngredientService.patch(allergyId, ingredientId, request));
    }

    @DeleteMapping("/allergy/{allergyId}/ingredient/{ingredientId}")
    public ResponseEntity<Void> delete(@PathVariable Long allergyId, @PathVariable Long ingredientId) {
        allergyIngredientService.delete(allergyId, ingredientId);
        return ResponseEntity.noContent().build();
    }
}
