package com.venus.crud.controller.jpa.fullstage;

import com.venus.crud.dto.jpa.response.fullstage.ReviewFullResponse;
import com.venus.crud.service.jpa.fullstage.ReviewFullService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reviews")
public class ReviewFullController {

    private final ReviewFullService reviewFullService;

    public ReviewFullController(ReviewFullService reviewFullService) {
        this.reviewFullService = reviewFullService;
    }

    @GetMapping("/{id}/full")
    public ResponseEntity<ReviewFullResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(reviewFullService.findById(id));
    }
}