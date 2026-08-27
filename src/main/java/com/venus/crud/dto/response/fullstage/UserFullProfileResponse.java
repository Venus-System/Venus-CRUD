package com.venus.crud.dto.response.fullstage;

import com.venus.crud.dto.response.shared.ProfileTagResponse;
import com.venus.crud.dto.response.user.UserProfileResponse;
import com.venus.crud.dto.response.user.UserResponse;
import java.util.List;

public record UserFullProfileResponse(
        UserResponse user,
        UserProfileResponse profile,
        List<ProfileTagResponse> tags
) {
}