package com.venus.crud.repository.jpa.media;

import com.venus.crud.entity.enums.MediaPurpose;
import com.venus.crud.entity.enums.MediaStatus;
import com.venus.crud.entity.media.MediaAsset;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MediaAssetRepository extends JpaRepository<MediaAsset, Long> {

    List<MediaStatus> LIVE_STATUSES = List.of(MediaStatus.PENDING, MediaStatus.ACTIVE);

    @EntityGraph(attributePaths = "user")
    Optional<MediaAsset> findByUserIdAndPurposeAndStatusIn(Long userId, MediaPurpose purpose, Collection<MediaStatus> statuses);

    @EntityGraph(attributePaths = "productVersion")
    List<MediaAsset> findByProductVersionIdAndPurposeAndStatusInOrderBySortOrderAscIdAsc(
            Long productVersionId, MediaPurpose purpose, Collection<MediaStatus> statuses);

    @Query("select coalesce(max(media.sortOrder), -1) from MediaAsset media "
            + "where media.productVersion.id = :productVersionId "
            + "and media.purpose = com.venus.crud.entity.enums.MediaPurpose.PRODUCT_PHOTO "
            + "and media.status in :statuses")
    int findMaxSortOrder(@Param("productVersionId") Long productVersionId, @Param("statuses") Collection<MediaStatus> statuses);
}
