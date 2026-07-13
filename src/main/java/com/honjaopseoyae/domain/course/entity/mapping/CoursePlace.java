package com.honjaopseoyae.domain.course.entity.mapping;

import java.time.LocalDate;

import com.honjaopseoyae.domain.course.entity.Course;
import com.honjaopseoyae.domain.course.entity.OrderType;
import com.honjaopseoyae.domain.place.entity.Place;
import com.honjaopseoyae.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

    private LocalDate date;

    @Enumerated(EnumType.STRING)
    private OrderType orderType;

    @Builder
    private CoursePlace(Course course, Place place, Long sortOrder,
                        String distance, String timeTaken, LocalDate date, OrderType orderType) {
        this.course = course;
        this.place = place;
        this.sortOrder = sortOrder;
        this.distance = distance;
        this.timeTaken = timeTaken;
        this.date = date;
        this.orderType = orderType;
    }

    public void updateSortOrder(Long sortOrder, OrderType orderType) {
        this.sortOrder = sortOrder;
        this.orderType = orderType;
    }

    public void updateDate(LocalDate date){this.date = date;}

    public void updateRouteInfo(String distance, String timeTaken) {
        this.distance = distance;
        this.timeTaken = timeTaken;
    }
}
