package com.honjaopseoyae.domain.facility.entity;


import com.honjaopseoyae.global.common.TargetType;
import com.honjaopseoyae.global.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 무장애(베리어프리) 편의시설 정보.
 * targetType + targetId 로 PLACE 또는 OLLE 를 가리킴
 */
@Entity
@Table(name = "barrier_facility")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BarrierFacility extends BaseEntity {

    @Enumerated(EnumType.STRING)
    private BarrierFacilityType facilityType;   // 장애 서비스 유형 (25가지)

    @Enumerated(EnumType.STRING)
    private TargetType targetType;              // 어떤 것에 대한 정보인지 (PLACE / OLLE)

    private Long targetId;                      // 위 타입의 id 값

    @Builder
    private BarrierFacility(BarrierFacilityType facilityType,
                            TargetType targetType, Long targetId) {
        this.facilityType = facilityType;
        this.targetType = targetType;
        this.targetId = targetId;
    }
}
