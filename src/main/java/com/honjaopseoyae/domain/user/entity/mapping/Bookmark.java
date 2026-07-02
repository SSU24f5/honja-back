package com.honjaopseoyae.domain.user.entity.mapping;


import com.honjaopseoyae.domain.course.entity.Course;
import com.honjaopseoyae.domain.user.entity.User;
import com.honjaopseoyae.global.entity.BaseEntity;
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
 * 즐겨찾기 (User ↔ Course 매핑 테이블, 내가 저장한 타인의 코스).
 */
@Entity
@Table(name = "bookmark")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Bookmark extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;                    // 유저 아이디

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;                // 타인의 코스 아이디

    @Builder
    private Bookmark(User user, Course course) {
        this.user = user;
        this.course = course;
    }
}
