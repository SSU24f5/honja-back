package com.honjaopseoyae.domain.user.entity;


import com.honjaopseoyae.global.common.PetSizeType;
import com.honjaopseoyae.global.common.RoleType;
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

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Column(nullable = false)
    private String nickname;              // 닉네임

    @Column(nullable = false)
    private String password;              // 비밀번호

    @Enumerated(EnumType.STRING)
    private RoleType role;                // 권한

    @Column(nullable = false, unique = true)
    private String email;                 // 이메일 (검증 로직은 서비스단에서)

    private String profile;               // 프로필 이미지 url

    private boolean petMode;              // 반려동물 활성화 여부

    private boolean seniorMode;           // 시니어 활성화 여부

    @Enumerated(EnumType.STRING)
    private PetSizeType petSize;          // 반려견 크기

    @Builder
    private User(String nickname, String email, String profile,
                 boolean petMode, boolean seniorMode, PetSizeType petSize, RoleType role, String password) {
        this.nickname = nickname;
        this.email = email;
        this.profile = profile;
        this.role = role;
        this.password = password;
        this.petMode = petMode;
        this.seniorMode = seniorMode;
        this.petSize = petSize;
    }
}
