package com.venus.crud.controller.jpa.user;

import com.venus.crud.dto.jpa.patch.user.UserListPatchRequest;
import com.venus.crud.dto.jpa.request.user.UserListRequest;
import com.venus.crud.dto.jpa.response.user.UserListResponse;
import com.venus.crud.entity.enums.ListType;
import com.venus.crud.service.jpa.user.UserListService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/user-lists")
@Tag(name = "Listas do Usuário", description = "Listas criadas pelo usuário e os produtos dentro delas.")
public class UserListController {

    private final UserListService userListService;

    public UserListController(UserListService userListService) {
        this.userListService = userListService;
    }

    @Operation(operationId = "userListFindAll", summary = "Lista as listas do usuário")
    @GetMapping
    public ResponseEntity<List<UserListResponse>> findAll() {
        return ResponseEntity.ok(userListService.findAll());
    }

    @Operation(operationId = "userListFindByUserId", summary = "Lista as listas de um usuário")
    @GetMapping("/user/{userId}")
    public ResponseEntity<Slice<UserListResponse>> findByUserId(
            @PathVariable Long userId,
            @RequestParam(required = false) ListType listType,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(userListService.findByUserId(userId, listType, pageable));
    }

    @Operation(operationId = "userListFindById", summary = "Busca a lista do usuário por id")
    @GetMapping("/{id}")
    public ResponseEntity<UserListResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(userListService.findById(id));
    }

    @Operation(operationId = "userListCreate", summary = "Cadastra uma lista do usuário")
    @PostMapping
    public ResponseEntity<UserListResponse> create(@Valid @RequestBody UserListRequest request) {
        UserListResponse created = userListService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @Operation(operationId = "userListUpdate", summary = "Substitui os dados da lista do usuário")
    @PutMapping("/{id}")
    public ResponseEntity<UserListResponse> update(@PathVariable Long id, @Valid @RequestBody UserListRequest request) {
        return ResponseEntity.ok(userListService.update(id, request));
    }

    @Operation(operationId = "userListPatch", summary = "Atualiza parcialmente a lista do usuário")
    @PatchMapping("/{id}")
    public ResponseEntity<UserListResponse> patch(@PathVariable Long id, @Valid @RequestBody UserListPatchRequest request) {
        return ResponseEntity.ok(userListService.patch(id, request));
    }

    @Operation(operationId = "userListDelete", summary = "Remove a lista do usuário")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userListService.delete(id);
        return ResponseEntity.noContent().build();
    }
}