package com.honjaopseoyae.domain.place.entity;


import com.honjaopseoyae.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 장소 (Tour API + Kakao 좌표 기반).
 */
@Entity
@Table(name = "place")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Place extends BaseEntity {

    private double mapx;                  // 위도 (kakao_x)

    private double mapy;                  // 경도 (kakao_y)

    private String image;                 // 이미지 s3 url

    private boolean petPlace;            // 반려동물 서비스 여부

    private boolean barrierFree;         // 베리어프리(노약자) 여부

    @Column(name = "content_id")
    private String contentId;            // tour api 에 등록된 content id

    private Integer contentType;         // 카테고리 코드

    @Builder
    private Place(double mapx, double mapy, String image, boolean petPlace,
                  boolean barrierFree, String contentId, Integer contentType) {
        this.mapx = mapx;
        this.mapy = mapy;
        this.image = image;
        this.petPlace = petPlace;
        this.barrierFree = barrierFree;
        this.contentId = contentId;
        this.contentType = contentType;
    }
}