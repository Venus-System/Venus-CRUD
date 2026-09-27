package com.venus.crud.mapper.jpa.user;

import com.venus.crud.config.VenusMapperConfig;
import com.venus.crud.dto.jpa.patch.user.UserProfilePatchRequest;
import com.venus.crud.dto.jpa.request.user.UserProfileRequest;
import com.venus.crud.dto.jpa.response.user.UserProfileResponse;
import com.venus.crud.entity.user.User;
import com.venus.crud.entity.user.UserProfile;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = VenusMapperConfig.class)
public interface UserProfileMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", source = "userId")
    UserProfile toEntity(UserProfileRequest request);

    @Mapping(target = "userId", source = "user.id")
    UserProfileResponse toResponse(UserProfile entity);

    @InheritConfiguration(name = "toEntity")
    void updateEntity(UserProfileRequest request, @MappingTarget UserProfile entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", source = "userId")
    @Mapping(target = "skinType", ignore = true)
    @Mapping(target = "skinPhototype", ignore = true)
    @Mapping(target = "hasHyperpigmentation", ignore = true)
    @Mapping(target = "hasMelasma", ignore = true)
    @Mapping(target = "hasRosacea", ignore = true)
    @Mapping(target = "hasEczema", ignore = true)
    @Mapping(target = "hairPattern", ignore = true)
    @Mapping(target = "scalpType", ignore = true)
    @Mapping(target = "skinSensitivity", ignore = true)
    @Mapping(target = "acneProne", ignore = true)
    @Mapping(target = "ageRange", ignore = true)
    @Mapping(target = "gender", ignore = true)
    @Mapping(target = "isPregnant", ignore = true)
    @Mapping(target = "isBreastfeeding", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchEntity(UserProfilePatchRequest request, @MappingTarget UserProfile entity);

    @AfterMapping
    default void patchAnswers(UserProfilePatchRequest request, @MappingTarget UserProfile entity) {
        request.skinType().ifPresent(entity::setSkinType);
        request.skinPhototype().ifPresent(entity::setSkinPhototype);
        request.hasHyperpigmentation().ifPresent(entity::setHasHyperpigmentation);
        request.hasMelasma().ifPresent(entity::setHasMelasma);
        request.hasRosacea().ifPresent(entity::setHasRosacea);
        request.hasEczema().ifPresent(entity::setHasEczema);
        request.hairPattern().ifPresent(entity::setHairPattern);
        request.scalpType().ifPresent(entity::setScalpType);
        request.skinSensitivity().ifPresent(entity::setSkinSensitivity);
        request.acneProne().ifPresent(entity::setAcneProne);
        request.ageRange().ifPresent(entity::setAgeRange);
        request.gender().ifPresent(entity::setGender);
        request.isPregnant().ifPresent(entity::setIsPregnant);
        request.isBreastfeeding().ifPresent(entity::setIsBreastfeeding);
    }

    default User mapUser(Long userId) {
        if (userId == null) {
            return null;
        }
        User user = new User();
        user.setId(userId);
        return user;
    }
}
