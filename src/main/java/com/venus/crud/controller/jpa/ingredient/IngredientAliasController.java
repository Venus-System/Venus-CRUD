package com.venus.crud.controller.jpa.ingredient;

import com.venus.crud.dto.jpa.patch.ingredient.IngredientAliasPatchRequest;
import com.venus.crud.dto.jpa.request.ingredient.IngredientAliasRequest;
import com.venus.crud.dto.jpa.response.ingredient.IngredientAliasResponse;
import com.venus.crud.entity.enums.SourceType;
import com.venus.crud.service.jpa.ingredient.IngredientAliasService;
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
@RequestMapping("/api/ingredient-aliases")
@Tag(name = "Sinônimos de Ingrediente", description = "Nomes alternativos de um mesmo ingrediente.")
public class IngredientAliasController {

    private final IngredientAliasService ingredientAliasService;

    public IngredientAliasController(IngredientAliasService ingredientAliasService) {
        this.ingredientAliasService = ingredientAliasService;
    }

    @Operation(operationId = "ingredientAliasFindAll", summary = "Lista os sinônimos de ingrediente")
    @GetMapping
    public ResponseEntity<List<IngredientAliasResponse>> findAll() {
        return ResponseEntity.ok(ingredientAliasService.findAll());
    }

    @Operation(operationId = "ingredientAliasSearch", summary = "Busca os sinônimos de ingrediente com filtros e paginação")
    @GetMapping("/search")
    public ResponseEntity<Slice<IngredientAliasResponse>> search(
            @RequestParam(required = false) String aliasLanguage,
            @RequestParam(required = false) SourceType sourceType,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ingredientAliasService.search(aliasLanguage, sourceType, pageable));
    }

    @Operation(operationId = "ingredientAliasFindByAliasName", summary = "Busca o sinônimo pelo nome")
    @GetMapping("/alias-name/{aliasName}")
    public ResponseEntity<IngredientAliasResponse> findByAliasName(
            @Parameter(description = "Nome alternativo exato do ingrediente.")
            @PathVariable String aliasName) {
        return ResponseEntity.ok(ingredientAliasService.findByAliasName(aliasName));
    }

    @Operation(operationId = "ingredientAliasFindByIngredientId", summary = "Lista os sinônimos de um ingrediente")
    @GetMapping("/ingredient/{ingredientId}")
    public ResponseEntity<List<IngredientAliasResponse>> findByIngredientId(@PathVariable Long ingredientId) {
        return ResponseEntity.ok(ingredientAliasService.findByIngredientId(ingredientId));
    }

    @Operation(operationId = "ingredientAliasFindById", summary = "Busca o sinônimo de ingrediente por id")
    @GetMapping("/{id}")
    public ResponseEntity<IngredientAliasResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ingredientAliasService.findById(id));
    }

    @Operation(operationId = "ingredientAliasCreate", summary = "Cadastra um sinônimo de ingrediente")
    @PostMapping
    public ResponseEntity<IngredientAliasResponse> create(@Valid @RequestBody IngredientAliasRequest request) {
        IngredientAliasResponse created = ingredientAliasService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @Operation(operationId = "ingredientAliasUpdate", summary = "Substitui os dados do sinônimo de ingrediente")
    @PutMapping("/{id}")
    public ResponseEntity<IngredientAliasResponse> update(@PathVariable Long id, @Valid @RequestBody IngredientAliasRequest request) {
        return ResponseEntity.ok(ingredientAliasService.update(id, request));
    }

    @Operation(operationId = "ingredientAliasPatch", summary = "Atualiza parcialmente o sinônimo de ingrediente")
    @PatchMapping("/{id}")
    public ResponseEntity<IngredientAliasResponse> patch(@PathVariable Long id, @Valid @RequestBody IngredientAliasPatchRequest request) {
        return ResponseEntity.ok(ingredientAliasService.patch(id, request));
    }

    @Operation(operationId = "ingredientAliasDelete", summary = "Remove o sinônimo de ingrediente")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ingredientAliasService.delete(id);
        return ResponseEntity.noContent().build();
    }
}