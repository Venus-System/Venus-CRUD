package com.venus.crud.dto.response.fullstage;

import com.venus.crud.dto.response.user.UserListItemResponse;
import com.venus.crud.dto.response.user.UserListResponse;
import java.util.List;

public record UserListWithItemsResponse(
        UserListResponse list,
        List<UserListItemResponse> items
) {
}