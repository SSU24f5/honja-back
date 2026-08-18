package com.honjaopseoyae.domain.course.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.honjaopseoyae.domain.course.dto.response.CourseInvitationResponseDto;
import com.honjaopseoyae.domain.course.entity.enums.CourseRole;
import com.honjaopseoyae.domain.course.entity.enums.InviteStatus;
import com.honjaopseoyae.domain.course.entity.mapping.CourseMember;

public interface CourseMemberRepository extends JpaRepository<CourseMember, Long> {

    List<CourseMember> findAllByUserIdAndStatus(Long userId, InviteStatus status);

    boolean existsByUserIdAndStatus(Long userId, InviteStatus status);

    Optional<CourseMember> findByCourseIdAndUserIdAndStatus(
            Long courseId,
            Long userId,
            InviteStatus status
    );

    Optional<CourseMember> findByCourseIdAndUserId(Long courseId, Long userId);

    Optional<CourseMember> findByCourseIdAndUserIdAndRoleAndStatus(
            Long courseId, Long userId, CourseRole role, InviteStatus status
    );

    @Query("SELECT new com.honjaopseoyae.domain.course.dto.response.CourseInvitationResponseDto(" +
            "  cm.id, c.id, c.name, c.description, ownerUser.nickname, ownerUser.email, cm.createdAt, cm.status" +
            ") " +
            "FROM CourseMember cm " +
            "JOIN cm.course c " +
            "JOIN CourseMember owner ON owner.course = c AND owner.role = :ownerRole " +
            "JOIN owner.user ownerUser " +
            "WHERE cm.user.id = :userId AND cm.status = :inviteStatus")
    List<CourseInvitationResponseDto> findMyInvitationsWithOwner(
            @Param("userId") Long userId,
            @Param("ownerRole") CourseRole ownerRole,
            @Param("inviteStatus") InviteStatus inviteStatus
    );

    @Query("SELECT new com.honjaopseoyae.domain.course.dto.response.CourseInvitationResponseDto(" +
            "  cm.id, c.id, c.name, c.description, cm.user.nickname, cm.user.email, cm.createdAt, cm.status" +
            ") " +
            "FROM CourseMember cm " +
            "JOIN cm.course c " +
            "WHERE cm.status = :inviteStatus " +
            "AND cm.role != :ownerRole " +
            "AND EXISTS (" +
            "  SELECT 1 FROM CourseMember owner " +
            "  WHERE owner.course = c AND owner.user.id = :ownerId AND owner.role = :ownerRole" +
            ")")
    List<CourseInvitationResponseDto> findSentInvitations(
            @Param("ownerId") Long ownerId,
            @Param("ownerRole") CourseRole ownerRole,
            @Param("inviteStatus") InviteStatus inviteStatus
    );
}