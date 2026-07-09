package com.honjaopseoyae.domain.course.entity.mapping;


import com.honjaopseoyae.domain.course.entity.Course;
import com.honjaopseoyae.domain.place.entity.Place;
import com.honjaopseoyae.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 코스에 담긴 장소
 */
@Entity
@Table(name = "course_place")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CoursePlace extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;                // 코스 아이디

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    private Place place;                  // 장소 아이디

    // 원본 컬럼명 'order' 는 SQL 예약어라 'sort_order' 로 지정했습니다.
    @Column(name = "sort_order")
    private Long sortOrder;              // 코스 내 순서

    @Builder
    private CoursePlace(Course course, Place place, Long sortOrder) {
        this.course = course;
        this.place = place;
        this.sortOrder = sortOrder;
    }

    public void updateSortOrder(Long sortOrder) {
        this.sortOrder = sortOrder;
    }
}
