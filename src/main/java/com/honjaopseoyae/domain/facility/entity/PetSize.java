package com.honjaopseoyae.domain.facility.entity;


import com.honjaopseoyae.global.common.PetSizeType;
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
 * 반려동물 허용 크기 정보.
 * targetType + targetId 로 PLACE 또는 OLLE 를 가리킴
 */
@Entity
@Table(name = "pet_size")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PetSize extends BaseEntity {

    @Enumerated(EnumType.STRING)
    private PetSizeType size;             // 반려동물 허용 크기

    @Enumerated(EnumType.STRING)
    private TargetType targetType;        // 어떤 곳에 대한 정보인지 (PLACE / OLLE)

    private Long targetId;               // 위 타입의 id 값

    @Builder
    private PetSize(PetSizeType size, TargetType targetType, Long targetId) {
        this.size = size;
        this.targetType = targetType;
        this.targetId = targetId;
    }
}
