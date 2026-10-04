package com.venus.crud.mapper.jpa.user;

import static org.assertj.core.api.Assertions.assertThat;

import com.venus.crud.dto.jpa.patch.user.UserListPatchRequest;
import com.venus.crud.entity.enums.ListCoverKey;
import com.venus.crud.entity.user.UserList;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;

class UserListMapperTest {

    private final UserListMapper mapper = new UserListMapperImpl();

    @Test
    void patchWithNullClearsDescriptionAndCover() {
        UserList userList = userList("Antes de dormir", ListCoverKey.SKINCARE);

        mapper.patchEntity(new UserListPatchRequest(null, null, null, JsonNullable.of(null), JsonNullable.of(null)), userList);

        assertThat(userList.getDescription()).isNull();
        assertThat(userList.getCoverKey()).isNull();
    }

    @Test
    void patchWithoutTheFieldsKeepsDescriptionAndCover() {
        UserList userList = userList("Antes de dormir", ListCoverKey.SKINCARE);

        mapper.patchEntity(new UserListPatchRequest(null, "Rotina da noite", null, JsonNullable.undefined(),
                JsonNullable.undefined()), userList);

        assertThat(userList.getName()).isEqualTo("Rotina da noite");
        assertThat(userList.getDescription()).isEqualTo("Antes de dormir");
        assertThat(userList.getCoverKey()).isEqualTo(ListCoverKey.SKINCARE);
    }

    @Test
    void patchWithValuesReplacesDescriptionAndCover() {
        UserList userList = userList("Antes de dormir", ListCoverKey.SKINCARE);

        mapper.patchEntity(new UserListPatchRequest(null, null, null, JsonNullable.of("Para o verao"),
                JsonNullable.of(ListCoverKey.FAVORITOS)), userList);

        assertThat(userList.getDescription()).isEqualTo("Para o verao");
        assertThat(userList.getCoverKey()).isEqualTo(ListCoverKey.FAVORITOS);
    }

    private UserList userList(String description, ListCoverKey coverKey) {
        UserList userList = new UserList();
        userList.setName("Rotina noturna");
        userList.setDescription(description);
        userList.setCoverKey(coverKey);
        return userList;
    }
}
