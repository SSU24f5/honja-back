package com.honjaopseoyae.domain.user.entity.mapping;


import com.honjaopseoyae.domain.olle.entity.Olle;
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
 * 추천 올레길 리스트 (User ↔ Olle 매핑 테이블).
 */
@Entity
@Table(name = "recommended_olle")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecommendedOlle extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;                    // 유저 아이디

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "olle_id")
    private Olle olle;                    // 올레길 아이디

    @Builder
    private RecommendedOlle(User user, Olle olle) {
        this.user = user;
        this.olle = olle;
    }
}
