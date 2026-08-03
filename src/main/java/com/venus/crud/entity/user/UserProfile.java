package com.venus.crud.entity.user;

import com.venus.crud.entity.enums.AgeRange;
import com.venus.crud.entity.enums.Gender;
import com.venus.crud.entity.enums.HairType;
import com.venus.crud.entity.enums.ScalpType;
import com.venus.crud.entity.enums.SensitivityLevel;
import com.venus.crud.entity.enums.SkinPhototype;
import com.venus.crud.entity.enums.SkinType;
import com.venus.crud.entity.shared.AuditableEntity;
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
@Table(name = "user_profiles")
@AttributeOverride(name = "id", column = @Column(name = "user_profile_id"))
public class UserProfile extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "skin_type", nullable = false)
    private SkinType skinType;

    @Column(name = "skin_phototype", nullable = false)
    private SkinPhototype skinPhototype;

    @Column(name = "has_hyperpigmentation", nullable = false)
    private Boolean hasHyperpigmentation;

    @Column(name = "has_melasma", nullable = false)
    private Boolean hasMelasma;

    @Column(name = "has_rosacea", nullable = false)
    private Boolean hasRosacea;

    @Column(name = "has_eczema", nullable = false)
    private Boolean hasEczema;

    @Column(name = "hair_type", nullable = false)
    private HairType hairType;

    @Column(name = "scalp_type", nullable = false)
    private ScalpType scalpType;

    @Column(name = "skin_sensitivity", nullable = false)
    private SensitivityLevel skinSensitivity;

    @Column(name = "acne_prone", nullable = false)
    private Boolean acneProne;

    @Column(name = "age_range", nullable = false)
    private AgeRange ageRange;

    @Column(name = "gender", nullable = false)
    private Gender gender;

    @Column(name = "is_pregnant", nullable = false)
    private Boolean isPregnant;
}
