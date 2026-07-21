package com.honjaopseoyae.domain.course.entity;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.honjaopseoyae.domain.course.entity.enums.CourseType;
import com.honjaopseoyae.domain.course.entity.enums.TripCategory;
import com.honjaopseoyae.domain.course.entity.mapping.CourseMember;
import com.honjaopseoyae.domain.user.entity.User;
import com.honjaopseoyae.global.entity.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자가 만든 코스.
 */
@Entity
@Table(name = "course")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Course extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;                    // 작성자

    private String name;                  // 이름

    private String description;           // 설명

    @Column(name = "is_public")
    private boolean isPublic;            // 공개 여부

    private LocalDate startDate;
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private CourseType courseType;

    @Enumerated(EnumType.STRING)
    private TripCategory tripCategory;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseMember> members = new ArrayList<>();


    @Builder
    private Course(User user, String name, String description,
                   boolean isPublic,
                   LocalDate startDate, LocalDate endDate,
                   CourseType courseType, TripCategory tripCategory) {
        this.user = user;
        this.name = name;
        this.description = description;
        this.isPublic = isPublic;
        this.startDate = startDate;
        this.endDate = endDate;
        this.courseType = courseType;
        this.tripCategory = tripCategory;
    }
}
