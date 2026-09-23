package com.venus.crud.controller.jpa.review;

import com.venus.crud.dto.jpa.patch.review.ReviewPatchRequest;
import com.venus.crud.dto.jpa.request.review.ReviewRequest;
import com.venus.crud.dto.jpa.response.review.ReviewResponse;
import com.venus.crud.service.jpa.review.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.math.BigDecimal;
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
@RequestMapping("/api/reviews")
@Tag(name = "Avaliações", description = "Avaliações de produto escritas por usuários, com o agregado de autor e votos.")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @Operation(operationId = "reviewFindAll", summary = "Lista as avaliações")
    @GetMapping
    public ResponseEntity<List<ReviewResponse>> findAll() {
        return ResponseEntity.ok(reviewService.findAll());
    }

    @Operation(operationId = "reviewSearch", summary = "Busca as avaliações com filtros e paginação")
    @GetMapping("/search")
    public ResponseEntity<Slice<ReviewResponse>> search(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(reviewService.search(pageable));
    }

    @Operation(operationId = "reviewFindByProductVersionId", summary = "Lista as avaliações de uma versão de produto")
    @GetMapping("/product-version/{productVersionId}")
    public ResponseEntity<Slice<ReviewResponse>> findByProductVersionId(
            @PathVariable Long productVersionId,
            @RequestParam(required = false) Boolean verifiedUse,
            @RequestParam(required = false) BigDecimal minRating,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(reviewService.findByProductVersionId(productVersionId, verifiedUse, minRating, pageable));
    }

    @Operation(operationId = "reviewFindByUserId", summary = "Lista as avaliações escritas por um usuário")
    @GetMapping("/user/{userId}")
    public ResponseEntity<Slice<ReviewResponse>> findByUserId(
            @PathVariable Long userId, @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(reviewService.findByUserId(userId, pageable));
    }

    @Operation(operationId = "reviewFindById", summary = "Busca a avaliação por id")
    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(reviewService.findById(id));
    }

    @Operation(operationId = "reviewCreate", summary = "Cadastra uma avaliação")
    @PostMapping
    public ResponseEntity<ReviewResponse> create(@Valid @RequestBody ReviewRequest request) {
        ReviewResponse created = reviewService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @Operation(operationId = "reviewUpdate", summary = "Substitui os dados da avaliação")
    @PutMapping("/{id}")
    public ResponseEntity<ReviewResponse> update(@PathVariable Long id, @Valid @RequestBody ReviewRequest request) {
        return ResponseEntity.ok(reviewService.update(id, request));
    }

    @Operation(operationId = "reviewPatch", summary = "Atualiza parcialmente a avaliação")
    @PatchMapping("/{id}")
    public ResponseEntity<ReviewResponse> patch(@PathVariable Long id, @Valid @RequestBody ReviewPatchRequest request) {
        return ResponseEntity.ok(reviewService.patch(id, request));
    }

    @Operation(operationId = "reviewDelete", summary = "Remove a avaliação")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reviewService.delete(id);
        return ResponseEntity.noContent().build();
    }
}