package com.venus.crud.controller.jpa.review;

import com.venus.crud.dto.jpa.patch.review.ReviewPatchRequest;
import com.venus.crud.dto.jpa.request.review.ReviewRequest;
import com.venus.crud.dto.jpa.response.review.ReviewResponse;
import com.venus.crud.service.jpa.review.ReviewService;
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
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public ResponseEntity<List<ReviewResponse>> findAll() {
        return ResponseEntity.ok(reviewService.findAll());
    }

    @GetMapping("/search")
    public ResponseEntity<Slice<ReviewResponse>> search(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(reviewService.search(pageable));
    }

    @GetMapping("/product-version/{productVersionId}")
    public ResponseEntity<Slice<ReviewResponse>> findByProductVersionId(
            @PathVariable Long productVersionId,
            @RequestParam(required = false) Boolean verifiedUse,
            @RequestParam(required = false) BigDecimal minRating,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(reviewService.findByProductVersionId(productVersionId, verifiedUse, minRating, pageable));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Slice<ReviewResponse>> findByUserId(
            @PathVariable Long userId, @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(reviewService.findByUserId(userId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(reviewService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ReviewResponse> create(@Valid @RequestBody ReviewRequest request) {
        ReviewResponse created = reviewService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReviewResponse> update(@PathVariable Long id, @Valid @RequestBody ReviewRequest request) {
        return ResponseEntity.ok(reviewService.update(id, request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ReviewResponse> patch(@PathVariable Long id, @Valid @RequestBody ReviewPatchRequest request) {
        return ResponseEntity.ok(reviewService.patch(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reviewService.delete(id);
        return ResponseEntity.noContent().build();
    }
}