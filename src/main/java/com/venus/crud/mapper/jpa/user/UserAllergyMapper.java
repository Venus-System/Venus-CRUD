package com.venus.crud.mapper.jpa.user;

import com.venus.crud.config.VenusMapperConfig;
import com.venus.crud.dto.jpa.patch.user.UserAllergyPatchRequest;
import com.venus.crud.dto.jpa.request.user.UserAllergyRequest;
import com.venus.crud.dto.jpa.response.user.UserAllergyResponse;
import com.venus.crud.entity.user.Allergy;
import com.venus.crud.entity.user.User;
import com.venus.crud.entity.user.UserAllergy;
import org.mapstruct.BeanMapping;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = VenusMapperConfig.class)
public interface UserAllergyMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", source = "userId")
    @Mapping(target = "allergy", source = "allergyId")
    UserAllergy toEntity(UserAllergyRequest request);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "allergyId", source = "allergy.id")
    UserAllergyResponse toResponse(UserAllergy entity);

    @InheritConfiguration(name = "toEntity")
    void updateEntity(UserAllergyRequest request, @MappingTarget UserAllergy entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", source = "userId")
    @Mapping(target = "allergy", source = "allergyId")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchEntity(UserAllergyPatchRequest request, @MappingTarget UserAllergy entity);

    default User mapUser(Long userId) {
        if (userId == null) {
            return null;
        }
        User user = new User();
        user.setId(userId);
        return user;
    }

    default Allergy mapAllergy(Long allergyId) {
        if (allergyId == null) {
            return null;
        }
        Allergy allergy = new Allergy();
        allergy.setId(allergyId);
        return allergy;
    }
}
