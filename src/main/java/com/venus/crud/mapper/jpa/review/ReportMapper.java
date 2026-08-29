package com.venus.crud.mapper.jpa.review;

import com.venus.crud.config.VenusMapperConfig;
import com.venus.crud.dto.jpa.patch.review.ReportPatchRequest;
import com.venus.crud.dto.jpa.request.review.ReportRequest;
import com.venus.crud.dto.jpa.response.review.ReportResponse;
import com.venus.crud.entity.admin.AdminUser;
import com.venus.crud.entity.review.Report;
import com.venus.crud.entity.user.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = VenusMapperConfig.class)
public interface ReportMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", source = "userId")
    @Mapping(target = "adminUser", source = "adminUserId")
    Report toEntity(ReportRequest request);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "adminUserId", source = "adminUser.id")
    ReportResponse toResponse(Report entity);

    @InheritConfiguration(name = "toEntity")
    void updateEntity(ReportRequest request, @MappingTarget Report entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", source = "userId")
    @Mapping(target = "adminUser", source = "adminUserId")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchEntity(ReportPatchRequest request, @MappingTarget Report entity);

    default User mapUser(Long userId) {
        if (userId == null) {
            return null;
        }
        User user = new User();
        user.setId(userId);
        return user;
    }

    default AdminUser mapAdminUser(Long adminUserId) {
        if (adminUserId == null) {
            return null;
        }
        AdminUser adminUser = new AdminUser();
        adminUser.setId(adminUserId);
        return adminUser;
    }
}
