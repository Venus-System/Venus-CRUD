package com.venus.crud.controller.jpa.user;

import com.venus.crud.dto.jpa.patch.user.UserPatchRequest;
import com.venus.crud.dto.jpa.request.user.UserRequest;
import com.venus.crud.dto.jpa.response.user.UserResponse;
import com.venus.crud.entity.enums.UserStatus;
import com.venus.crud.service.jpa.user.UserService;
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
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/search")
    public ResponseEntity<Slice<UserResponse>> search(
            @RequestParam(required = false) UserStatus status,
            @RequestParam(required = false) String name,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(userService.search(status, name, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest request) {
        UserResponse created = userService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(@PathVariable Long id, @Valid @RequestBody UserRequest request) {
        return ResponseEntity.ok(userService.update(id, request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserResponse> patch(@PathVariable Long id, @Valid @RequestBody UserPatchRequest request) {
        return ResponseEntity.ok(userService.patch(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}