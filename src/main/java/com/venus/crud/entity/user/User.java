package com.venus.crud.entity.user;

import com.venus.crud.entity.enums.UserStatus;
import com.venus.crud.entity.shared.AuditableEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@Entity
@Table(name = "users")
@AttributeOverride(name = "id", column = @Column(name = "user_id"))
public class User extends AuditableEntity {

    private static final int COORDINATE_PLACES = 2;

    @Column(name = "firebase_uid", nullable = false, unique = true)
    private String firebaseUid;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "status", nullable = false)
    private UserStatus status;

    @Column(name = "last_login")
    private OffsetDateTime lastLogin;

    @Column(name = "latitude", precision = 5, scale = 2)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 5, scale = 2)
    private BigDecimal longitude;

    @Column(name = "city")
    private String city;

    @Column(name = "state")
    private String state;

    public void setLatitude(BigDecimal latitude) {
        this.latitude = roundToTwoPlaces(latitude);
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = roundToTwoPlaces(longitude);
    }

    private static BigDecimal roundToTwoPlaces(BigDecimal coordinate) {
        if (coordinate == null) {
            return null;
        }
        return coordinate.setScale(COORDINATE_PLACES, RoundingMode.HALF_UP);
    }
}
