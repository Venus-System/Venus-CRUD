package com.venus.crud.mapper.user;

import com.venus.crud.config.VenusMapperConfig;
import com.venus.crud.dto.request.user.FavoritePatchRequest;
import com.venus.crud.dto.request.user.FavoriteRequest;
import com.venus.crud.dto.response.user.FavoriteResponse;
import com.venus.crud.entity.product.Product;
import com.venus.crud.entity.user.Favorite;
import com.venus.crud.entity.user.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = VenusMapperConfig.class)
public interface FavoriteMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "user", source = "userId")
    @Mapping(target = "product", source = "productId")
    Favorite toEntity(FavoriteRequest request);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "productId", source = "product.id")
    FavoriteResponse toResponse(Favorite entity);

    @InheritConfiguration(name = "toEntity")
    void updateEntity(FavoriteRequest request, @MappingTarget Favorite entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "user", source = "userId")
    @Mapping(target = "product", source = "productId")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchEntity(FavoritePatchRequest request, @MappingTarget Favorite entity);

    default User mapUser(Long userId) {
        if (userId == null) {
            return null;
        }
        User user = new User();
        user.setId(userId);
        return user;
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
