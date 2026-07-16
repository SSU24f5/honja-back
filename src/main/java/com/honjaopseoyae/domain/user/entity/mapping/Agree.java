package com.honjaopseoyae.domain.user.entity.mapping;


import com.honjaopseoyae.domain.user.entity.User;
import com.honjaopseoyae.global.entity.BaseEntity;
import com.honjaopseoyae.domain.term.entity.Term;
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
 * 약관 동의 내역 (User ↔ Term 매핑 테이블).
 */
@Entity
@Table(name = "agree")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Agree extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;                  // 멤버 아이디

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "term_id")
    private Term term;                     // 약관 아이디

    @Builder
    private Agree(User user, Term term) {
        this.user = user;
        this.term = term;
    }
}