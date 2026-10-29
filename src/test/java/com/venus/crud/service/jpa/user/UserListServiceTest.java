package com.venus.crud.service.jpa.user;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.venus.crud.entity.user.UserList;
import com.venus.crud.exception.ResourceNotFoundException;
import com.venus.crud.mapper.jpa.user.UserListMapperImpl;
import com.venus.crud.repository.jpa.user.UserListRepository;
import com.venus.crud.service.jpa.media.MediaAssetService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserListServiceTest {

    private static final long USER_LIST_ID = 7L;

    @Mock
    private UserListRepository userListRepository;

    @Mock
    private MediaAssetService mediaAssetService;

    private UserListService service;

    @BeforeEach
    void setUp() {
        service = new UserListService(userListRepository, new UserListMapperImpl(), mediaAssetService);
    }

    @Test
    void deletingTheListRemovesTheCoverFirst() {
        UserList userList = new UserList();
        userList.setId(USER_LIST_ID);
        when(userListRepository.findById(USER_LIST_ID)).thenReturn(Optional.of(userList));

        service.delete(USER_LIST_ID);

        InOrder deletionOrder = inOrder(mediaAssetService, userListRepository);
        deletionOrder.verify(mediaAssetService).deleteListCoverIfExists(USER_LIST_ID);
        deletionOrder.verify(userListRepository).deleteById(USER_LIST_ID);
    }

    @Test
    void listThatDoesNotExistReturns404WithoutTouchingTheCover() {
        when(userListRepository.findById(USER_LIST_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(USER_LIST_ID)).isInstanceOf(ResourceNotFoundException.class);

        verifyNoInteractions(mediaAssetService);
    }
}
