package com.honjaopseoyae.domain.olle.entity;


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
 * 올레길 (제주 올레 코스).
 */
@Entity
@Table(name = "olle")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Olle extends BaseEntity {

    private String name;                  // 코스 이름

    private Long code;                    // 코스 번호

    private double distance;              // 코스 길이 (km)

    private String description;           // 설명

    private boolean barrierFree;         // 베리어 프리 서비스 제공 여부(하나라도 해당되면 true)

    private double startX;               // 시작 위도

    private double startY;               // 시작 경도

    private double endX;                 // 끝나는 지점의 위도

    private double endY;                 // 끝나는 지점의 경도

    @Enumerated(EnumType.STRING)
    private Level level;                  // 난이도

    private boolean hasUphill;           // 오르막 여부

    private boolean petAllowed;          // 반려동물 허용 여부

    @Builder
    private Olle(String name, Long code, double distance, String description,
                 boolean barrierFree, double startX, double startY,
                 double endX, double endY, Level level,
                 boolean hasUphill, boolean petAllowed) {
        this.name = name;
        this.code = code;
        this.distance = distance;
        this.description = description;
        this.barrierFree = barrierFree;
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.level = level;
        this.hasUphill = hasUphill;
        this.petAllowed = petAllowed;
    }
}