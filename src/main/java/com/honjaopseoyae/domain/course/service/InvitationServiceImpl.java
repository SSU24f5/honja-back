package com.honjaopseoyae.domain.course.service;

import com.honjaopseoyae.domain.course.dto.request.SendInvitationRequestDto;
import com.honjaopseoyae.domain.course.dto.response.CourseInvitationResponseDto;
import com.honjaopseoyae.domain.course.entity.Course;
import com.honjaopseoyae.domain.course.entity.enums.CourseRole;
import com.honjaopseoyae.domain.course.entity.enums.InviteStatus;
import com.honjaopseoyae.domain.course.entity.mapping.CourseMember;
import com.honjaopseoyae.domain.course.repository.CourseMemberRepository;
import com.honjaopseoyae.domain.course.repository.CourseRepository;
import com.honjaopseoyae.domain.user.entity.User;
import com.honjaopseoyae.domain.user.repository.UserRepository;
import com.honjaopseoyae.global.apipayload.domain.CourseErrorStatus;
import com.honjaopseoyae.global.apipayload.domain.InvitationErrorStatus;
import com.honjaopseoyae.global.apipayload.domain.UserErrorStatus;
import com.honjaopseoyae.global.apipayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InvitationServiceImpl implements InvitationService {

    private final CourseMemberRepository courseMemberRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    @Override
    public List<CourseInvitationResponseDto> getReceivedInvitations(User user) {
        return courseMemberRepository.findMyInvitationsWithOwner(
                user.getId(), CourseRole.OWNER, InviteStatus.PENDING
        );
    }

    @Override
    public List<CourseInvitationResponseDto> getSentInvitations(User user) {
        return courseMemberRepository.findSentInvitations(
                user.getId(), CourseRole.OWNER, InviteStatus.PENDING
        );
    }

    @Transactional
    @Override
    public void sendInvitations(Long courseId, SendInvitationRequestDto dto, User sender) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new GeneralException(CourseErrorStatus.COURSE_NOT_FOUND));

        courseMemberRepository.findByCourseIdAndUserIdAndRoleAndStatus(
                courseId, sender.getId(), CourseRole.OWNER, InviteStatus.ACCEPTED
        ).orElseThrow(() -> new GeneralException(CourseErrorStatus.COURSE_ACCESS_DENIED));

        for (Long receiverId : dto.receiverIds()) {
            if (receiverId.equals(sender.getId())) {
                log.warn("자기 자신을 초대하려는 시도 - senderId: {}", sender.getId());
                continue;
            }

            boolean alreadyExists = courseMemberRepository.findByCourseIdAndUserId(courseId, receiverId).isPresent();
            if (alreadyExists) {
                log.info("이미 초대되었거나 참여중인 사용자 - courseId: {}, receiverId: {}", courseId, receiverId);
                continue;
            }

            User receiver = userRepository.findById(receiverId)
                    .orElseThrow(() -> new GeneralException(UserErrorStatus.USER_NOT_FOUND));

            CourseMember courseMember = CourseMember.builder()
                    .course(course)
                    .user(receiver)
                    .role(CourseRole.MEMBER)
                    .status(InviteStatus.PENDING)
                    .build();

            courseMemberRepository.save(courseMember);
        }
    }

    @Transactional
    @Override
    public void acceptInvitation(Long courseMemberId, User user) {
        CourseMember courseMember = getOrThrow(courseMemberId);
        validateReceiver(courseMember, user);
        validatePending(courseMember);
        courseMember.accept();
    }

    @Transactional
    @Override
    public void rejectInvitation(Long courseMemberId, User user) {
        CourseMember courseMember = getOrThrow(courseMemberId);
        validateReceiver(courseMember, user);
        validatePending(courseMember);
        courseMember.reject();
    }

    @Transactional
    @Override
    public void cancelInvitation(Long courseMemberId, User user) {
        CourseMember courseMember = getOrThrow(courseMemberId);

        courseMemberRepository.findByCourseIdAndUserIdAndRoleAndStatus(
                courseMember.getCourse().getId(), user.getId(), CourseRole.OWNER, InviteStatus.ACCEPTED
        ).orElseThrow(() -> new GeneralException(CourseErrorStatus.COURSE_ACCESS_DENIED));

        courseMemberRepository.delete(courseMember);
    }

    private CourseMember getOrThrow(Long courseMemberId) {
        return courseMemberRepository.findById(courseMemberId)
                .orElseThrow(() -> new GeneralException(CourseErrorStatus.COURSE_NOT_WRITER));
    }

    private void validateReceiver(CourseMember courseMember, User user) {
        if (!courseMember.getUser().getId().equals(user.getId())) {
            throw new GeneralException(CourseErrorStatus.COURSE_ACCESS_DENIED);
        }
    }

    private void validatePending(CourseMember courseMember) {
        if (courseMember.getStatus() != InviteStatus.PENDING) {
            throw new GeneralException(InvitationErrorStatus.INVITATION_ALREADY_PROCESSED);
        }
    }
}