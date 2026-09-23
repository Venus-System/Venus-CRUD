package com.venus.crud.controller.jpa.ingredient;

import com.venus.crud.dto.jpa.patch.ingredient.IngredientPatchRequest;
import com.venus.crud.dto.jpa.request.ingredient.IngredientRequest;
import com.venus.crud.dto.jpa.response.ingredient.IngredientResponse;
import com.venus.crud.service.jpa.ingredient.IngredientService;
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
@RequestMapping("/api/ingredients")
@Tag(name = "Ingredientes", description = "Catálogo INCI e o agregado com categoria, efeitos e propriedades.")
public class IngredientController {

    private final IngredientService ingredientService;

    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    @Operation(operationId = "ingredientFindAll", summary = "Lista os ingredientes")
    @GetMapping
    public ResponseEntity<List<IngredientResponse>> findAll() {
        return ResponseEntity.ok(ingredientService.findAll());
    }

    @Operation(
            operationId = "ingredientSearch",
            summary = "Busca os ingredientes com filtros e paginação",
            description = "Os filtros **não se combinam**: vale o primeiro preenchido, nesta ordem — `commonName`, "
                    + "`ingredientCategoryId`, `categoryName`, `minIrritationRiskLevel`. Sem nenhum, lista "
                    + "todos.\n\n`categoryName` traz também os ingredientes das **subcategorias diretas** da categoria "
                    + "informada, e devolve 404 se não existir categoria com esse nome.")
    @GetMapping("/search")
    public ResponseEntity<Slice<IngredientResponse>> search(
            @RequestParam(required = false) String commonName,
            @RequestParam(required = false) Long ingredientCategoryId,
            @Parameter(description = "Nome da categoria; traz também os ingredientes das subcategorias diretas.")
            @RequestParam(required = false) String categoryName,
            @RequestParam(required = false) Short minIrritationRiskLevel,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ingredientService.search(commonName, ingredientCategoryId, categoryName,
                minIrritationRiskLevel, pageable));
    }

    @Operation(operationId = "ingredientFindByInciName", summary = "Busca o ingrediente pelo nome INCI")
    @GetMapping("/inci-name/{inciName}")
    public ResponseEntity<IngredientResponse> findByInciName(
            @Parameter(description = "Nome INCI exato do ingrediente, como aparece no rótulo.")
            @PathVariable String inciName) {
        return ResponseEntity.ok(ingredientService.findByInciName(inciName));
    }

    @Operation(
            operationId = "ingredientFindBySourceReference",
            summary = "Busca o ingrediente pela referência da fonte externa")
    @GetMapping("/source-reference/{sourceReference}")
    public ResponseEntity<IngredientResponse> findBySourceReference(
            @Parameter(description = "Identificador do ingrediente na fonte externa de onde ele foi importado.")
            @PathVariable String sourceReference) {
        return ResponseEntity.ok(ingredientService.findBySourceReference(sourceReference));
    }

    @Operation(operationId = "ingredientFindById", summary = "Busca o ingrediente por id")
    @GetMapping("/{id}")
    public ResponseEntity<IngredientResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ingredientService.findById(id));
    }

    @Operation(operationId = "ingredientCreate", summary = "Cadastra um ingrediente")
    @PostMapping
    public ResponseEntity<IngredientResponse> create(@Valid @RequestBody IngredientRequest request) {
        IngredientResponse created = ingredientService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @Operation(operationId = "ingredientUpdate", summary = "Substitui os dados do ingrediente")
    @PutMapping("/{id}")
    public ResponseEntity<IngredientResponse> update(@PathVariable Long id, @Valid @RequestBody IngredientRequest request) {
        return ResponseEntity.ok(ingredientService.update(id, request));
    }

    @Operation(operationId = "ingredientPatch", summary = "Atualiza parcialmente o ingrediente")
    @PatchMapping("/{id}")
    public ResponseEntity<IngredientResponse> patch(@PathVariable Long id, @Valid @RequestBody IngredientPatchRequest request) {
        return ResponseEntity.ok(ingredientService.patch(id, request));
    }

    @Operation(operationId = "ingredientDelete", summary = "Remove o ingrediente")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ingredientService.delete(id);
        return ResponseEntity.noContent().build();
    }
}