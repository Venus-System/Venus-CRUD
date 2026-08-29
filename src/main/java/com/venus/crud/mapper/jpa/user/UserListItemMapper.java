package com.venus.crud.mapper.jpa.user;

import com.venus.crud.config.VenusMapperConfig;
import com.venus.crud.dto.jpa.patch.user.UserListItemPatchRequest;
import com.venus.crud.dto.jpa.request.user.UserListItemRequest;
import com.venus.crud.dto.jpa.response.user.UserListItemResponse;
import com.venus.crud.entity.product.Product;
import com.venus.crud.entity.user.UserList;
import com.venus.crud.entity.user.UserListItem;
import org.mapstruct.BeanMapping;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = VenusMapperConfig.class)
public interface UserListItemMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "userList", source = "userListId")
    @Mapping(target = "product", source = "productId")
    UserListItem toEntity(UserListItemRequest request);

    @Mapping(target = "userListId", source = "userList.id")
    @Mapping(target = "productId", source = "product.id")
    UserListItemResponse toResponse(UserListItem entity);

    @InheritConfiguration(name = "toEntity")
    void updateEntity(UserListItemRequest request, @MappingTarget UserListItem entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "userList", source = "userListId")
    @Mapping(target = "product", source = "productId")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchEntity(UserListItemPatchRequest request, @MappingTarget UserListItem entity);

    default UserList mapUserList(Long userListId) {
        if (userListId == null) {
            return null;
        }
        UserList userList = new UserList();
        userList.setId(userListId);
        return userList;
    }

    default Product mapProduct(Long productId) {
        if (productId == null) {
            return null;
        }
        Product product = new Product();
        product.setId(productId);
        return product;
    }
}
