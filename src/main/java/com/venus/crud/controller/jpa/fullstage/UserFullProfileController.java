package com.venus.crud.controller.jpa.fullstage;

import com.venus.crud.dto.jpa.response.fullstage.UserFullProfileResponse;
import com.venus.crud.service.jpa.fullstage.UserFullProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserFullProfileController {

    private final UserFullProfileService userFullProfileService;

    public UserFullProfileController(UserFullProfileService userFullProfileService) {
        this.userFullProfileService = userFullProfileService;
    }

    @GetMapping("/{userId}/full-profile")
    public ResponseEntity<UserFullProfileResponse> findByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(userFullProfileService.findByUserId(userId));
    }
}