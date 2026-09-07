package com.venus.crud.entity.media;

import com.venus.crud.entity.enums.MediaDeliveryType;
import com.venus.crud.entity.enums.MediaPurpose;
import com.venus.crud.entity.enums.MediaStatus;
import com.venus.crud.entity.product.ProductVersion;
import com.venus.crud.entity.shared.AuditableEntity;
import com.venus.crud.entity.user.User;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@Entity
@Table(name = "media_assets")
@AttributeOverride(name = "id", column = @Column(name = "media_asset_id"))
public class MediaAsset extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_product_version_id")
    private ProductVersion productVersion;

    @Column(name = "purpose", nullable = false)
    private MediaPurpose purpose;

    @Column(name = "provider", nullable = false)
    private String provider;

    @Column(name = "resource_type", nullable = false)
    private String resourceType;

    @Column(name = "delivery_type", nullable = false)
    private MediaDeliveryType deliveryType;

    @Column(name = "public_id", nullable = false)
    private String publicId;

    @Column(name = "asset_id")
    private String assetId;

    @Column(name = "version")
    private Long version;

    @Column(name = "secure_url")
    private String secureUrl;

    @Column(name = "format")
    private String format;

    @Column(name = "width")
    private Integer width;

    @Column(name = "height")
    private Integer height;

    @Column(name = "bytes")
    private Long bytes;

    @Column(name = "folder")
    private String folder;

    @Column(name = "original_filename")
    private String originalFilename;

    @Column(name = "alt_text")
    private String altText;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(name = "status", nullable = false)
    private MediaStatus status;
}
