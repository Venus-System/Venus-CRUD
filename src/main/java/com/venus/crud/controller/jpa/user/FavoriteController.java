package com.venus.crud.controller.jpa.user;

import com.venus.crud.dto.jpa.request.user.FavoriteRequest;
import com.venus.crud.dto.jpa.response.user.FavoriteResponse;
import com.venus.crud.service.jpa.user.FavoriteService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @GetMapping
    public ResponseEntity<List<FavoriteResponse>> findAll() {
        return ResponseEntity.ok(favoriteService.findAll());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Slice<FavoriteResponse>> findByUserId(
            @PathVariable Long userId, @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(favoriteService.findByUserId(userId, pageable));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<Slice<FavoriteResponse>> findByProductId(
            @PathVariable Long productId, @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(favoriteService.findByProductId(productId, pageable));
    }

    @GetMapping("/product/{productId}/count")
    public ResponseEntity<Long> countByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(favoriteService.countByProductId(productId));
    }

    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Long> countByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(favoriteService.countByUserId(userId));
    }

    @PostMapping
    public ResponseEntity<FavoriteResponse> create(@Valid @RequestBody FavoriteRequest request) {
        FavoriteResponse created = favoriteService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/user/{userId}")
                .buildAndExpand(created.userId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @DeleteMapping("/user/{userId}/product/{productId}")
    public ResponseEntity<Void> delete(@PathVariable Long userId, @PathVariable Long productId) {
        favoriteService.delete(userId, productId);
        return ResponseEntity.noContent().build();
    }
}