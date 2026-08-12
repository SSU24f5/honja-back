package com.honjaopseoyae.domain.place.entity;

import com.honjaopseoyae.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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


    private double mapx;                  // 경도 (kakao_x / 길찾기 정제 좌표)

    private double mapy;                  // 위도 (kakao_y / 길찾기 정제 좌표)

    private double tourMapx;              // Tour API 원본 경도

    private double tourMapy;              // Tour API 원본 위도

    private String image;                 // 이미지 s3 url

    private boolean petPlace;            // 반려동물 서비스 여부

    private boolean barrierFree;         // 베리어프리(노약자) 여부

    @Column(name = "content_id")
    private String contentId;            // tour api 에 등록된 content id

    private Integer contentType;         // 카테고리 코드

    private String cat3;

    @Enumerated(EnumType.STRING)
    private PlaceType placeType;

    @Column(name = "title")
    private String title;

    @Column(name = "indoor")
    private boolean indoor; //true: 실내, false: 실외

    @Builder
    private Place(double mapx, double mapy, double tourMapx, double tourMapy, String image, boolean petPlace,
        boolean barrierFree, String contentId, Integer contentType, String cat3, PlaceType placeType, String title,
        boolean indoor) {
        this.mapx = mapx;
        this.mapy = mapy;
        this.tourMapx = tourMapx == 0.0 ? mapx : tourMapx;
        this.tourMapy = tourMapy == 0.0 ? mapy : tourMapy;
        this.image = image;
        this.petPlace = petPlace;
        this.barrierFree = barrierFree;
        this.contentId = contentId;
        this.contentType = contentType;
        this.cat3 = cat3;
        this.placeType = placeType;
        this.title = title;
        this.indoor = indoor;
    }

    public void update(double mapx, double mapy, String image, boolean petPlace, boolean barrierFree,
        Integer contentType, String title, boolean indoor, String cat3) {
        this.title = title;
        this.mapx = mapx;
        this.mapy = mapy;
        this.image = image;
        this.petPlace = petPlace;
        this.barrierFree = barrierFree;
        this.contentType = contentType;
        this.title = title;
        this.indoor = indoor;
        this.cat3 = cat3;
    }

    public double getLat() {
        return this.mapy;
    }

    public double getLng() {
        return this.mapx;
    }

    public void updateCoordinates(double mapx, double mapy) {
        this.mapx = mapx;
        this.mapy = mapy;
    }
}
