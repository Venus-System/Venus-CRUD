package com.venus.crud.controller.jpa.review;

import com.venus.crud.dto.jpa.patch.review.ReviewVotePatchRequest;
import com.venus.crud.dto.jpa.request.review.ReviewVoteRequest;
import com.venus.crud.dto.jpa.response.review.ReviewVoteResponse;
import com.venus.crud.entity.enums.VoteType;
import com.venus.crud.service.jpa.review.ReviewVoteService;
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
@RequestMapping("/api/review-votes")
public class ReviewVoteController {

    private final ReviewVoteService reviewVoteService;

    public ReviewVoteController(ReviewVoteService reviewVoteService) {
        this.reviewVoteService = reviewVoteService;
    }

    @GetMapping
    public ResponseEntity<List<ReviewVoteResponse>> findAll() {
        return ResponseEntity.ok(reviewVoteService.findAll());
    }

    @GetMapping("/search")
    public ResponseEntity<Slice<ReviewVoteResponse>> search(
            @RequestParam(required = false) VoteType voteType,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(reviewVoteService.search(voteType, pageable));
    }

    @GetMapping("/review/{reviewId}")
    public ResponseEntity<Slice<ReviewVoteResponse>> findByReviewId(
            @PathVariable Long reviewId, @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(reviewVoteService.findByReviewId(reviewId, pageable));
    }

    @GetMapping("/review/{reviewId}/count")
    public ResponseEntity<Long> countByReviewIdAndVoteType(@PathVariable Long reviewId, @RequestParam VoteType voteType) {
        return ResponseEntity.ok(reviewVoteService.countByReviewIdAndVoteType(reviewId, voteType));
    }

    @GetMapping("/review/{reviewId}/user/{userId}")
    public ResponseEntity<ReviewVoteResponse> findByReviewIdAndUserId(@PathVariable Long reviewId, @PathVariable Long userId) {
        return ResponseEntity.ok(reviewVoteService.findByReviewIdAndUserId(reviewId, userId));
    }

    @PostMapping
    public ResponseEntity<ReviewVoteResponse> create(@Valid @RequestBody ReviewVoteRequest request) {
        ReviewVoteResponse created = reviewVoteService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/review/{reviewId}/user/{userId}")
                .buildAndExpand(created.reviewId(), created.userId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PatchMapping("/review/{reviewId}/user/{userId}")
    public ResponseEntity<ReviewVoteResponse> patch(
            @PathVariable Long reviewId, @PathVariable Long userId, @Valid @RequestBody ReviewVotePatchRequest request) {
        return ResponseEntity.ok(reviewVoteService.patch(reviewId, userId, request));
    }

    @DeleteMapping("/review/{reviewId}/user/{userId}")
    public ResponseEntity<Void> delete(@PathVariable Long reviewId, @PathVariable Long userId) {
        reviewVoteService.delete(reviewId, userId);
        return ResponseEntity.noContent().build();
    }
}