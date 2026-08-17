package com.venus.crud.mapper.user;

import com.venus.crud.config.VenusMapperConfig;
import com.venus.crud.dto.request.user.RoutineItemPatchRequest;
import com.venus.crud.dto.request.user.RoutineItemRequest;
import com.venus.crud.dto.response.user.RoutineItemResponse;
import com.venus.crud.entity.product.Product;
import com.venus.crud.entity.user.Routine;
import com.venus.crud.entity.user.RoutineItem;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = VenusMapperConfig.class)
public interface RoutineItemMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "routine", source = "routineId")
    @Mapping(target = "product", source = "productId")
    RoutineItem toEntity(RoutineItemRequest request);

    @Mapping(target = "routineId", source = "routine.id")
    @Mapping(target = "productId", source = "product.id")
    RoutineItemResponse toResponse(RoutineItem entity);

    @InheritConfiguration(name = "toEntity")
    void updateEntity(RoutineItemRequest request, @MappingTarget RoutineItem entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "routine", source = "routineId")
    @Mapping(target = "product", source = "productId")
    void patchEntity(RoutineItemPatchRequest request, @MappingTarget RoutineItem entity);

    default Routine mapRoutine(Long routineId) {
        if (routineId == null) {
            return null;
        }
        Routine routine = new Routine();
        routine.setId(routineId);
        return routine;
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
