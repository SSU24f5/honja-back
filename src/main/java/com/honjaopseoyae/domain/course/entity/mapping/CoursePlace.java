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

@Entity
@Table(name = "course_place")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CoursePlace extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    private Place place;

    @Column(name = "sort_order")
    private Long sortOrder;

    private String distance;

    private String timeTaken;

    @Builder
    private CoursePlace(Course course, Place place, Long sortOrder,
                        String distance, String timeTaken) {
        this.course = course;
        this.place = place;
        this.sortOrder = sortOrder;
        this.distance = distance;
        this.timeTaken = timeTaken;
    }

    public void updateSortOrder(Long sortOrder) {
        this.sortOrder = sortOrder;
    }

    public void updateRouteInfo(String distance, String timeTaken) {
        this.distance = distance;
        this.timeTaken = timeTaken;
    }
}
