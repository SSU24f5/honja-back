package com.honjaopseoyae.domain.term.entity;


import com.honjaopseoyae.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 약관.
 */
@Entity
@Table(name = "term")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Term extends BaseEntity {

    private String title;                 // 제목

    private String content;               // 내용

    @Column(name = "is_agreed")
    private boolean isAgreed;             // 동의 여부(필수 여부)

    @Builder
    private Term(String title, String content, boolean isAgreed) {
        this.title = title;
        this.content = content;
        this.isAgreed = isAgreed;
    }
}
