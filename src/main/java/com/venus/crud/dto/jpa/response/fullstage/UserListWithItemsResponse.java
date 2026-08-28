package com.venus.crud.dto.jpa.response.fullstage;

import com.venus.crud.dto.jpa.response.user.UserListItemResponse;
import com.venus.crud.dto.jpa.response.user.UserListResponse;
import java.util.List;

public record UserListWithItemsResponse(
        UserListResponse list,
        List<UserListItemResponse> items
) {
}