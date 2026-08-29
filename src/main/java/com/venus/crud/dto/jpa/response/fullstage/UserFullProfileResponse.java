package com.venus.crud.dto.jpa.response.fullstage;

import com.venus.crud.dto.jpa.response.product.ProductResponse;
import com.venus.crud.dto.jpa.response.shared.ProfileTagResponse;
import com.venus.crud.dto.jpa.response.user.UserPreferenceResponse;
import com.venus.crud.dto.jpa.response.user.UserProfileResponse;
import com.venus.crud.dto.jpa.response.user.UserResponse;
import java.util.List;

public record UserFullProfileResponse(
        UserResponse user,
        UserProfileResponse profile,
        List<ProfileTagResponse> tags,
        UserPreferenceResponse preferences,
        List<UserAllergyDetailResponse> allergies,
        List<ProductResponse> favorites,
        List<UserListWithItemsResponse> lists
) {
}
