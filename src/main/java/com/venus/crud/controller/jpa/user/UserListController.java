package com.venus.crud.controller.jpa.user;

import com.venus.crud.dto.jpa.patch.user.UserListPatchRequest;
import com.venus.crud.dto.jpa.request.user.UserListRequest;
import com.venus.crud.dto.jpa.response.user.UserListResponse;
import com.venus.crud.entity.enums.ListType;
import com.venus.crud.service.jpa.user.UserListService;
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
@RequestMapping("/api/user-lists")
public class UserListController {

    private final UserListService userListService;

    public UserListController(UserListService userListService) {
        this.userListService = userListService;
    }

    @GetMapping
    public ResponseEntity<List<UserListResponse>> findAll() {
        return ResponseEntity.ok(userListService.findAll());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Slice<UserListResponse>> findByUser(
            @PathVariable Long userId,
            @RequestParam(required = false) ListType listType,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(userListService.findByUser(userId, listType, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserListResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(userListService.findById(id));
    }

    @PostMapping
    public ResponseEntity<UserListResponse> create(@Valid @RequestBody UserListRequest request) {
        UserListResponse created = userListService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserListResponse> update(@PathVariable Long id, @Valid @RequestBody UserListRequest request) {
        return ResponseEntity.ok(userListService.update(id, request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserListResponse> patch(@PathVariable Long id, @Valid @RequestBody UserListPatchRequest request) {
        return ResponseEntity.ok(userListService.patch(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userListService.delete(id);
        return ResponseEntity.noContent().build();
    }
}