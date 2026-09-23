package com.venus.crud.controller.jpa.fullstage;

import com.venus.crud.dto.jpa.response.fullstage.ReviewFullResponse;
import com.venus.crud.service.jpa.fullstage.ReviewFullService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reviews")
@Tag(name = "Avaliações", description = "Avaliações de produto escritas por usuários, com o agregado de autor e votos.")
public class ReviewFullController {

    private final ReviewFullService reviewFullService;

    public ReviewFullController(ReviewFullService reviewFullService) {
        this.reviewFullService = reviewFullService;
    }

    @Operation(operationId = "reviewFullFindById", summary = "Busca a avaliação completa por id")
    @GetMapping("/{id}/full")
    public ResponseEntity<ReviewFullResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(reviewFullService.findById(id));
    }
}