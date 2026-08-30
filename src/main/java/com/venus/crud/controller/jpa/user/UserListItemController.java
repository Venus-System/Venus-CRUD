package com.venus.crud.controller.jpa.user;

import com.venus.crud.dto.jpa.patch.user.UserListItemPatchRequest;
import com.venus.crud.dto.jpa.request.user.UserListItemRequest;
import com.venus.crud.dto.jpa.response.user.UserListItemResponse;
import com.venus.crud.service.jpa.user.UserListItemService;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/user-list-items")
public class UserListItemController {

    private final UserListItemService userListItemService;

    public UserListItemController(UserListItemService userListItemService) {
        this.userListItemService = userListItemService;
    }

    @GetMapping
    public ResponseEntity<List<UserListItemResponse>> findAll() {
        return ResponseEntity.ok(userListItemService.findAll());
    }

    @GetMapping("/user-list/{userListId}")
    public ResponseEntity<List<UserListItemResponse>> findByUserListId(@PathVariable Long userListId) {
        return ResponseEntity.ok(userListItemService.findByUserListId(userListId));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<Slice<UserListItemResponse>> findByProductId(
            @PathVariable Long productId, @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(userListItemService.findByProductId(productId, pageable));
    }

    @PostMapping
    public ResponseEntity<UserListItemResponse> create(@Valid @RequestBody UserListItemRequest request) {
        UserListItemResponse created = userListItemService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/user-list/{userListId}")
                .buildAndExpand(created.userListId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PatchMapping("/user-list/{userListId}/product/{productId}")
    public ResponseEntity<UserListItemResponse> patch(
            @PathVariable Long userListId, @PathVariable Long productId, @Valid @RequestBody UserListItemPatchRequest request) {
        return ResponseEntity.ok(userListItemService.patch(userListId, productId, request));
    }

    @DeleteMapping("/user-list/{userListId}/product/{productId}")
    public ResponseEntity<Void> delete(@PathVariable Long userListId, @PathVariable Long productId) {
        userListItemService.delete(userListId, productId);
        return ResponseEntity.noContent().build();
    }
}